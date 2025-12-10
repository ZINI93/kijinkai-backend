package com.kijinkai.domain.payment.application.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;
import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
import com.kijinkai.domain.payment.application.port.in.withdraw.CreateWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.DeleteWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.GetWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.in.withdraw.UpdateWithdrawUseCase;
import com.kijinkai.domain.payment.application.port.out.WithdrawPersistenceRequestPort;
import com.kijinkai.domain.payment.domain.calculator.PaymentCalculator;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import com.kijinkai.domain.payment.domain.exception.*;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.model.WithdrawRequest;
import com.kijinkai.domain.payment.domain.util.PaymentContents;
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

    //----- 출금  -----

    /**
     * 출금 생성
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Transactional
    @Override
    public WithdrawResponseDto processWithdrawRequest(UUID userUuid, WithdrawRequestDto requestDto) {

        log.info("Creating withdraw request for user uuid: {}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);

        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());
        wallet.isActive(); // 지갑 활성화 상태

        BigDecimal withdrawFee;

        // 요청 금액이 3만엔 이상일 경우 수수료 0, 이하일경우 300엔
        if (requestDto.getRequestAmount().compareTo(new BigDecimal(30000)) > 0 ){
            withdrawFee = BigDecimal.ZERO;
        } else  {
            withdrawFee = PaymentContents.WITHDRAWAL_FEE;
        }

        ExchangeRateResponseDto exchangeRate = exchangeRateService.getExchangeRateInfoByCurrency(requestDto.getCurrency());

        BigDecimal convertedAmount = paymentCalculator.calculateWithdrawInJyp(requestDto.getCurrency(), requestDto.getRequestAmount());


        WithdrawRequest withdrawRequest = paymentFactory.createWithdrawRequest(
                customer, wallet, requestDto.getRequestAmount(), requestDto.getCurrency(), withdrawFee,
                requestDto.getBankName(), requestDto.getAccountHolder(), convertedAmount, requestDto.getAccountNumber(), exchangeRate.getRate()
        );
        withdrawRequest.validateWithdrawEligibility(requestDto);  // 2만엔 보다 적을시 출금 요청 제한

        if (wallet.getBalance().compareTo(withdrawRequest.getRequestAmount().add(withdrawFee)) <= 0){
            throw new PaymentAmountException("현재 잔액보다 큰 금액은 출금하지 못합니다.");
        }

        WithdrawRequest savedWithdrawRequest = withdrawPersistenceRequestPort.saveWithdrawRequest(withdrawRequest);

        log.info("Created withdraw request for request uuid: {}", withdrawRequest.getRequestUuid());

        return paymentMapper.createWithdrawResponse(savedWithdrawRequest);
    }

    /**
     * 출금 요청 승인
     *
     * @param requestUuid
     * @param adminUuid
     * @param requestDto
     * @return
     */
    @Transactional
    @Override
    public WithdrawResponseDto approveWithdrawRequest(UUID requestUuid, UUID adminUuid, WithdrawRequestDto requestDto) {
        log.info("Start withdraw approval process for request uuid: {}", requestUuid);

        //　관리자 조회 후 검증
        User admin = findUserByUserUuid(adminUuid);
        admin.validateAdminRole();

        WithdrawRequest withdrawRequest = findWithdrawRequestByRequestUuid(requestUuid);

        try {
            withdrawRequest.approve(adminUuid, requestDto.getMemo());

            WalletResponseDto wallet = updateWalletUseCase.withdrawal(
                    withdrawRequest.getCustomerUuid(),
                    withdrawRequest.getWalletUuid(),
                    withdrawRequest.getTotalDeductAmount());

            log.info("Completed withdraw approval process for request uuid: {} , withdraw amount: {}", requestUuid, withdrawRequest.getConvertedAmount());
            return paymentMapper.approvedWithdrawResponse(withdrawRequest, wallet);

        } catch (InsufficientBalanceException e) {
            log.error("Insufficient balance for deposit approval: {}", requestUuid, e);
            withdrawRequest.markAsFailed( "잔액 부족:" + e.getMessage());
            throw new WithdrawApprovalException("잔액이 부족합니다", e);
        } catch (WalletNotActiveException e) {
            log.error("WalletJpaEntity not active for deposit approval: {}", requestUuid, e);
            withdrawRequest.markAsFailed( "비활성된 지갑:" + e.getMessage());
            throw new WalletNotActiveException("지갑이 비활성 상태입니다", e);
        } catch (WithdrawRequestNotFoundException | WithdrawRequestStatusException e) {
            log.error("Invalid withdraw request for approval: {} ", requestUuid, e);
            throw e;
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (Exception e) {
            log.error("Unexpected error during deposit approval: {}", requestUuid, e);
            withdrawRequest.markAsFailed( "시스템 오류:" + e.getMessage());
            throw new PaymentProcessingException("출금 승인 중 예상치 못한 오류가 발생했습니다", e);
        }

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
     * @param adminUuid
     * @param depositorName
     * @param pageable
     * @return
     */
    @Override
    public Page<WithdrawResponseDto> getWithdrawByApprovalPending(UUID adminUuid, String depositorName, Pageable pageable) {
        User admin = findUserByUserUuid(adminUuid);
        admin.validateAdminRole();

        Page<WithdrawRequest> withdrawRequests = withdrawPersistenceRequestPort.findAllByWithdrawStatus(depositorName, WithdrawStatus.PENDING_ADMIN_APPROVAL, pageable);
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
