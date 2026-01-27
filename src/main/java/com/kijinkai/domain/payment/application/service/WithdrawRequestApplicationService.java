package com.kijinkai.domain.payment.application.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;
import com.kijinkai.domain.payment.application.handler.WithdrawFailedEvent;
import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
import com.kijinkai.domain.payment.application.port.in.withdraw.CreateWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.DeleteWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.GetWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.UpdateWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.out.WithdrawPersistenceRequestPort;
import com.kijinkai.domain.payment.domain.calculator.PaymentCalculator;
import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import com.kijinkai.domain.payment.domain.exception.*;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.model.WithdrawRequest;
import com.kijinkai.domain.payment.domain.util.PaymentContents;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.UUID;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class WithdrawRequestApplicationService implements CreateWithdrawUseCase, GetWithdrawUseCase, UpdateWithdrawUseCase, DeleteWithdrawUseCase {


    private final CustomerPersistencePort customerPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final WalletPersistencePort walletPersistencePort;
    private final WithdrawPersistenceRequestPort withdrawPersistenceRequestPort;

    private final ExchangeRateService exchangeRateService;
    private final PaymentCalculator paymentCalculator;
    private final PaymentMapper paymentMapper;
    private final PaymentFactory paymentFactory;

    private final UpdateWalletUseCase updateWalletUseCase;
    private final TransactionService transactionService;

    private final GenerateBusinessItemCode generateBusinessItemCode;

    private final ApplicationEventPublisher eventPublisher;

    //----- 생성.  -----

    /**
     * 유저 출금 요청
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Transactional
    @Override
    public WithdrawResponseDto processWithdrawRequest(UUID userUuid, WithdrawRequestDto requestDto) {

        log.info("Creating withdraw request for user uuid: {}", userUuid);

        // 1000원 이상 출금 가능
        if (requestDto.getRequestAmount().compareTo(BigDecimal.valueOf(1000)) < 0) {
            throw new PaymentAmountException("1000원 이상 결제가 가능합니다.");
        }

        // 유저 조회
        Customer customer = findCustomerByUserUuid(userUuid);


        // 락 적용 상태에서 지갑 조회 및 검증
        Wallet wallet = walletPersistencePort.findByCustomerUuidWithLock(customer.getCustomerUuid())
                .orElseThrow(() -> new WalletNotFoundException("Not found wallet"));

        wallet.validateActive();

        if (wallet.getBalance().compareTo(requestDto.getRequestAmount()) < 0) {
            throw new PaymentAmountException("현재 잔액보다 큰 금액은 출금하지 못합니다.");
        }


        updateWalletUseCase.withdrawal(customer.getCustomerUuid(), requestDto.getRequestAmount());


        String withdrawCode = generateBusinessItemCode.generateBusinessCode(userUuid.toString(), BusinessCodeType.WIT);

        // 요청 생성
        WithdrawRequest withdrawRequest = paymentFactory.createWithdrawRequest(
                customer,
                wallet,
                requestDto.getRequestAmount(),
                Currency.KRW,
                BigDecimal.ZERO,
                requestDto.getBankType(),
                requestDto.getAccountHolder(),
                requestDto.getAccountNumber(),
                withdrawCode
        );

        // 저장
        WithdrawRequest savedWithdraw = withdrawPersistenceRequestPort.saveWithdrawRequest(withdrawRequest);

        //거래 기록 저장
        transactionService.createAccountHistory(
                savedWithdraw.getCustomerUuid(),
                savedWithdraw.getWalletUuid(),
                TransactionType.WITHDRAWAL,
                savedWithdraw.getWithdrawCode(),
                savedWithdraw.getRequestAmount(),
                TransactionStatus.REQUEST
        );

        return paymentMapper.createWithdrawResponse(savedWithdraw);

    }

    // --- 업데이트

    /**
     * 출금 요청 승인 -> 추가적으로 하루 이상 미승인된 결제내역의 스케줄 관리 구현이 필요함
     *
     * @param adminUserUuid
     * @param requestUuid
     * @param requestDto
     * @return
     */
    @Transactional
    @Override
    public WithdrawResponseDto approveWithdrawRequest(UUID adminUserUuid, UUID requestUuid, WithdrawRequestDto requestDto) {
        log.info("Start withdraw approval process for request uuid: {}", requestUuid);

        //　관리자 조회 후 검증
        User admin = findUserByUserUuid(adminUserUuid);
        admin.validateAdminRole();

        WithdrawRequest withdrawRequest = findWithdrawRequestByRequestUuid(requestUuid);

        try {
            WithdrawRequest approveWithdraw = processWithdrawApprove(admin.getUserUuid(), withdrawRequest, requestDto);

            return paymentMapper.approvedWithdrawResponse(approveWithdraw);

        } catch (Exception e) {
            log.error("Error occurred, publishing failure event...", e);
            eventPublisher.publishEvent(new WithdrawFailedEvent(withdrawRequest.getRequestUuid(), e.getMessage()));
            throw e;
        }

    }

    // 승인절차 프로세스
    private WithdrawRequest processWithdrawApprove(UUID adminUserUuid, WithdrawRequest withdrawRequest, WithdrawRequestDto requestDto) {

        // 검증 및 승인처리
        withdrawRequest.approve(adminUserUuid, requestDto.getMemo());

        // 거래 기록갱신
        transactionService.completedPayment(withdrawRequest.getCustomerUuid(), withdrawRequest.getWithdrawCode());

        // 저장
        withdrawPersistenceRequestPort.saveWithdrawRequest(withdrawRequest);


        return withdrawRequest;

    }


    /**
     * 관리자가 유저의 출금 요청 정보 조회
     *
     * @param requestUuid
     * @param adminUuid
     * @return
     */
    @Override
    public WithdrawResponseDto getWithdrawInfoByAdmin(UUID requestUuid, UUID adminUuid) {

        User admin = findUserByUserUuid(adminUuid);
        admin.validateAdminRole();

        WithdrawRequest withdrawRequest = findWithdrawRequestByRequestUuid(requestUuid);
        log.info("Retrieved withdraw request for request uuid: {} by admin", requestUuid);

        return paymentMapper.withdrawInfoResponse(withdrawRequest);
    }

    /**
     * 유저 - 출금 리스트 조회
     *
     * @param adminUuid
     * @param pageable
     * @return
     */
    @Override
    public Page<WithdrawResponseDto> getWithdraws(UUID userUuid, Pageable pageable) {
        User user = findUserByUserUuid(userUuid);
        Customer customer = findCustomerByUserUuid(user.getUserUuid());
        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());

        Page<WithdrawRequest> withdraws = withdrawPersistenceRequestPort.findAllByCustomerUuid(customer.getCustomerUuid(), pageable);

        return withdraws.map(withdraw -> paymentMapper.withdrawDetailsInfo(withdraw, wallet.getBalance()));
    }

    /**
     * 관리자 - 승인대기 리스트
     */
    @Override
    public Page<WithdrawResponseDto> getWithdrawsByStatus(UUID adminUuid, WithdrawStatus status, Pageable pageable) {

        // 관리자 권한 검증
        User admin = findUserByUserUuid(adminUuid);
        admin.validateAdminRole();

        //조회
        Page<WithdrawRequest> withdrawRequests = withdrawPersistenceRequestPort.findAllByStatus(status, pageable);

        return withdrawRequests.map(paymentMapper::withdrawInfoResponse);

    }

    /**
     * 유저가 본인 출금 요청 정보 조회
     *
     * @param requestUuid
     * @param userUuid
     * @return
     */
    @Override
    public WithdrawResponseDto getWithdrawInfo(UUID requestUuid, UUID userUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);
        WithdrawRequest withdrawRequest = findWithdrawRequestByRequestUuidAndUserUuid(requestUuid, customer.getCustomerUuid());

        log.info("Retrieved withdraw request for request uuid: {}", requestUuid);
        return paymentMapper.withdrawInfoResponse(withdrawRequest);
    }

    private Wallet findWalletByCustomerUuid(UUID customerUuid) {
        return walletPersistencePort.findByCustomerUuid(customerUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found exception for customerUuid: %s", customerUuid)));
    }

    // helper method

    private WithdrawRequest findWithdrawRequestByRequestUuid(UUID requestUuid) {
        return withdrawPersistenceRequestPort.findByRequestUuid(requestUuid)
                .orElseThrow(() -> new WithdrawRequestNotFoundException(String.format("Withdraw request not found for request uuid: %s", requestUuid)));
    }

    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found exception for userUuid: %s", userUuid)));
    }

    private WithdrawRequest findWithdrawRequestByRequestUuidAndUserUuid(UUID requestUuid, UUID customerUuid) {
        return withdrawPersistenceRequestPort.findByRequestUuidAndCustomerUuid(requestUuid, customerUuid)
                .orElseThrow(() -> new WithdrawRequestNotFoundException(String.format("Withdraw request not found for request uuid: %s", requestUuid)));
    }


    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
    }
}
