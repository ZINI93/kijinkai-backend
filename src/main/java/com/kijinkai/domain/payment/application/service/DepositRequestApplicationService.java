package com.kijinkai.domain.payment.application.service;


import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
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
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final ExchangeRateService exchangeRateService;
    private final UpdateWalletUseCase updateWalletUseCase;


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

        log.info("Creating deposit request for user uuid: {}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);

        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());
        wallet.isActive();

        ExchangeRateResponseDto exchangeRate = exchangeRateService.getExchangeRateInfoByCurrency(requestDto.getOriginalCurrency());
        BigDecimal convertedAmount = paymentCalculator.calculateDepositInJpy(exchangeRate.getCurrency(), requestDto.getAmountOriginal());

        DepositRequest request = depositRequestFactory.createDepositRequest(customer, wallet, requestDto.getAmountOriginal(), requestDto.getOriginalCurrency(),
                convertedAmount, exchangeRate.getRate(), requestDto.getDepositorName(), requestDto.getBankType());
        request.validateDepositAmount();


        DepositRequest saveDepositRequest = depositRequestPersistencePort.saveDepositRequest(request);

        log.info("Created deposit request for request uuid: {}", saveDepositRequest.getRequestUuid());

        return paymentMapper.createDepositResponse(saveDepositRequest);
    }



    //입금 page 조회/
    /**
     *  유저 - 본인 입금 정보 조회
     *
     * @param requestUuid
     * @param userUuid
     * @return
     */
    @Override
    public DepositRequestResponseDto getDepositRequestInfo(UUID requestUuid, UUID userUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);

        DepositRequest depositRequest = depositRequestPersistencePort.findByCustomerUuidAndRequestUuid(customer.getCustomerUuid(),requestUuid)
                        .orElseThrow(() -> new DepositNotFoundException(String.format("DepositRequest not found for customerUuid: %s and requestUuid: %s", customer.getCustomerUuid(), requestUuid )));

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
     * 관리자 - 대기중인 거래의 내역 조회
     * 유저 검색 -> 관리자 권한 부여 -> Pending 상태의 거래를 page로 조회
     * @param userUuid
     * @param pageable
     * @return
     */
    @Override
    public Page<DepositRequestResponseDto> getDepositsByApprovalPendingByAdmin(UUID userUuid, Pageable pageable) {

        User admin = findUserByUserUuid(userUuid);
        admin.validateAdminRole();

        Page<DepositRequest> depositRequestsByPendingAdminApproval = depositRequestPersistencePort.findAllByStatus(DepositStatus.PENDING_ADMIN_APPROVAL, pageable);

        return depositRequestsByPendingAdminApproval.map(paymentMapper::depositInfo);
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
    public DepositRequestResponseDto approveDepositRequest(UUID requestUuid, UUID adminUuid, DepositRequestDto requestDto) {
        log.info("Start deposit approval process for request uuid: {}", requestUuid);

        User admin = findUserByUserUuid(adminUuid);
        admin.validateAdminRole();

        DepositRequest depositRequest = findDepositRequestByRequestUuid(requestUuid);;

        try {

            depositRequest.approve(adminUuid, requestDto.getMemo());

            WalletResponseDto wallet = updateWalletUseCase.deposit(
                    depositRequest.getCustomerUuid(),
                    depositRequest.getWalletUuid(),
                    depositRequest.getAmountConverted());

            DepositRequest savedRequest = depositRequestPersistencePort.saveDepositRequest(depositRequest);

            log.info("Completed deposit approval process for request uuid: {} , charged amount: {}", requestUuid, depositRequest.getAmountConverted());

            return paymentMapper.approveDepositResponse(savedRequest, wallet);

        } catch (InsufficientBalanceException e) {
            log.error("Insufficient balance for deposit approval: {}", requestUuid, e);
            depositRequest.markAsFailed("잔액 부족: " + e.getMessage());
            throw new DepositApprovalException("잔액이 부족합니다", e);

        } catch (WalletNotActiveException e) {
            log.error("WalletJpaEntity not active for deposit approval: {}", requestUuid, e);
            depositRequest.markAsFailed("비활성 지갑: " + e.getMessage());
            throw new DepositApprovalException("지갑이 비활성 상태입니다", e);

        } catch (DepositRequestNotFoundException | DepositRequestStatusException e) {
            log.error("Invalid deposit request for approval: {}", requestUuid, e);
            throw e;

        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");

        } catch (Exception e) {
            log.error("Unexpected error during deposit approval: {}", requestUuid, e);
            depositRequest.markAsFailed("비활성 지갑: " + e.getMessage());
            throw new PaymentProcessingException("입금 승인 중 예상치 못한 오류가 발생했습니다", e);
        }
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
}
