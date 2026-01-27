package com.kijinkai.domain.payment.application.service;


import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
import com.kijinkai.domain.payment.application.handler.DepositFailedEvent;
import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
import com.kijinkai.domain.payment.application.port.in.deposit.CreateDepositUseCase;
import com.kijinkai.domain.payment.application.port.in.deposit.DeleteDepositUseCase;
import com.kijinkai.domain.payment.application.port.in.deposit.GetDepositUseCase;
import com.kijinkai.domain.payment.application.port.in.deposit.UpdateDepositUseCase;
import com.kijinkai.domain.payment.application.port.out.DepositRequestPersistencePort;
import com.kijinkai.domain.payment.domain.calculator.PaymentCalculator;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.payment.domain.exception.*;
import com.kijinkai.domain.payment.domain.factory.DepositRequestFactory;
import com.kijinkai.domain.payment.domain.model.DepositRequest;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.transaction.service.TransactionService;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.application.port.in.UpdateWalletUseCase;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.domain.exception.InsufficientBalanceException;
import com.kijinkai.domain.wallet.domain.exception.WalletNotActiveException;
import com.kijinkai.domain.wallet.domain.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import com.kijinkai.util.BusinessCodeType;
import com.kijinkai.util.GenerateBusinessItemCode;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DepositRequestApplicationService implements CreateDepositUseCase, GetDepositUseCase, UpdateDepositUseCase, DeleteDepositUseCase {

    private final DepositRequestPersistencePort depositRequestPersistencePort;
    private final CustomerPersistencePort customerPersistencePort;
    private final WalletPersistencePort walletPersistencePort;
    private final UserPersistencePort userPersistencePort;

    private final PaymentCalculator paymentCalculator;
    private final PaymentMapper paymentMapper;
    private final DepositRequestFactory depositRequestFactory;


    private final UpdateWalletUseCase updateWalletUseCase;
    private final TransactionService transactionService;
    private final GenerateBusinessItemCode generateBusinessItemCode;

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 입금 요청 생성
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public DepositRequestResponseDto processDepositRequest(UUID userUuid, DepositRequestDto requestDto) {

        // 요청 ->
        log.info("Creating deposit request for user uuid: {}", userUuid);

        // 구매자 조회
        Customer customer = findCustomerByUserUuid(userUuid);

        //입금 코드 생성
        String depositCode = generateBusinessItemCode.generateBusinessCode(userUuid.toString(), BusinessCodeType.DEP);

        // 자갑 조회 및 검증
        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());
        wallet.isActive();


        // deposit request 생성 및 검증
        DepositRequest request = depositRequestFactory.createDepositRequest(
                customer,
                wallet,
                requestDto.getAmountOriginal(),
                requestDto.getDepositorName(),
                requestDto.getBankType(),
                depositCode
        );

        // 검증
        request.validateDepositAmount();

        // 저장
        DepositRequest saveDepositRequest = depositRequestPersistencePort.saveDepositRequest(request);


        // 거래내역 생성
        UUID accountHistory = transactionService.createAccountHistory(
                saveDepositRequest.getCustomerUuid(),
                saveDepositRequest.getWalletUuid(),
                TransactionType.DEPOSIT,
                saveDepositRequest.getDepositCode(),
                saveDepositRequest.getAmountOriginal(),
                TransactionStatus.REQUEST
        );

        if (accountHistory == null) {
            throw new PaymentProcessingException("거래 내역이 저장되지 않았습니다.");
        }

        log.info("Created deposit request for request uuid: {}", saveDepositRequest.getRequestUuid());

        return paymentMapper.createDepositResponse(saveDepositRequest);
    }


    /**
     * 입금 요청 승인
     * 관리자가 입금을 확인 후에 승인
     *
     * @param requestUuid
     * @param adminUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public DepositRequestResponseDto approveDepositRequest(UUID userUuid, UUID depositUuid, DepositRequestDto requestDto) {
        log.info("Start deposit approval process for request uuid: {}", depositUuid);

        // 관리자 권한 검증
        User adminUser = findUserByUserUuid(userUuid);
        adminUser.validateAdminRole();

        //deposit uuid로 조회
        DepositRequest depositRequest = findDepositRequestByRequestUuid(depositUuid);
        ;

        try {
            return processApproval(adminUser, requestDto, depositRequest);

        } catch (Exception e) {
            log.error("Error occurred, publishing failure event...", e);

            eventPublisher.publishEvent(new DepositFailedEvent(depositUuid, e.getMessage()));
            throw e;
        }
    }

    private DepositRequestResponseDto processApproval(User admin, DepositRequestDto requestDto, DepositRequest depositRequest) {

        // 상태 변경 및 승인 관리자 저장
        depositRequest.approve(admin.getUserUuid(), requestDto.getMemo());

        // 지갑에 금액 추가
        WalletResponseDto wallet = updateWalletUseCase.deposit(
                depositRequest.getCustomerUuid(),
                depositRequest.getWalletUuid(),
                depositRequest.getAmountOriginal()
        );

        //저장
        DepositRequest savedRequest = depositRequestPersistencePort.saveDepositRequest(depositRequest);

        // 거래내역저장
        transactionService.completedPayment(savedRequest.getCustomerUuid(), savedRequest.getDepositCode());

        log.info("Completed deposit approval process for request uuid: {} , charged amount: {}", depositRequest.getRequestUuid(), depositRequest.getAmountOriginal());

        return paymentMapper.approveDepositResponse(savedRequest, wallet);
    }


    //입금 page 조회/

    /**
     * 유저 - 본인 입금 정보 조회
     *
     * @param requestUuid
     * @param userUuid
     * @return
     */
    @Override
    public DepositRequestResponseDto getDepositRequestInfo(UUID requestUuid, UUID userUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);

        DepositRequest depositRequest = depositRequestPersistencePort.findByCustomerUuidAndRequestUuid(customer.getCustomerUuid(), requestUuid)
                .orElseThrow(() -> new DepositNotFoundException(String.format("DepositRequest not found for customerUuid: %s and requestUuid: %s", customer.getCustomerUuid(), requestUuid)));

        log.info("retrieved deposit request for request uuid: {} by user", requestUuid);
        return paymentMapper.depositInfoResponse(depositRequest);
    }


    //입금 page 조회/

    /**
     * 관리자가 유저 거래정보 조회
     *
     * @param requestUuid
     * @param userUuid
     * @return
     */
    @Override
    public DepositRequestResponseDto getDepositRequestInfoByAdmin(UUID requestUuid, UUID adminUuid) {
        // admin account in user
        User admin = findUserByUserUuid(adminUuid);
        admin.validateAdminRole();

        DepositRequest depositRequest = findDepositRequestByRequestUuid(requestUuid);

        log.info("retrieved deposit request for request uuid: {} by admin", requestUuid);
        return paymentMapper.depositInfoResponse(depositRequest);
    }

    /**
     * 싱태별 유저들 거래 조회
     * 유저 검색 -> 관리자 권한 부여 -> Pending 상태의 거래를 page로 조회
     *
     * @param userUuid
     * @param pageable
     * @return
     */
    @Override
    public Page<DepositRequestResponseDto> getDepositsByStatus(UUID userUuid, DepositStatus status, Pageable pageable) {

        //관리자 조회 및 검증
        User adminUser = findUserByUserUuid(userUuid);
        adminUser.validateAdminRole();

        //
        Page<DepositRequest> depositRequestsByStatus = depositRequestPersistencePort.findAllByStatus(status, pageable);

        return depositRequestsByStatus.map(paymentMapper::depositInfo);
    }


    @Override
    public Page<DepositRequestResponseDto> getDeposits(UUID userUuid, Pageable pageable) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Page<DepositRequest> depositRequests = depositRequestPersistencePort.findAllByCustomerUuid(customer.getCustomerUuid(), pageable);

        return depositRequests.map(paymentMapper::depositInfoResponse);
    }


    /**
     * 시간 지난 결제 전체 만료처리
     *
     * @return
     */
    @Override
    public List<DepositRequestResponseDto> expireOldRequests() {
        List<DepositRequest> pendingRequests = depositRequestPersistencePort.findByStatus(DepositStatus.PENDING_ADMIN_APPROVAL);
        List<DepositRequest> expiredRequests = pendingRequests.stream()
                .filter(DepositRequest::isExpired)
                .peek(DepositRequest::expire)
                .collect(Collectors.toList());

        List<DepositRequest> savedRefund = depositRequestPersistencePort.saveAllDeposit(expiredRequests);

        return savedRefund.stream().map(paymentMapper::depositInfoResponse).collect(Collectors.toList());
    }


    //helper method
    private Wallet findWalletByCustomerUuid(UUID customerUuid) {
        return walletPersistencePort.findByCustomerUuid(customerUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found exception for customerUuid: %s", customerUuid)));
    }

    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found exception for userUuid: %s", userUuid)));
    }

    private DepositRequest findDepositRequestByRequestUuid(UUID requestUuid) {
        return depositRequestPersistencePort.findByRequestUuid(requestUuid)
                .orElseThrow(() -> new DepositNotFoundException(String.format("Deposit request not found for request uuid: %s", requestUuid)));
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
    }

    // 핸들러

}
