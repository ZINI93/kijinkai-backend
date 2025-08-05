package com.kijinkai.domain.payment.domain.validator;

import com.kijinkai.domain.payment.application.dto.PaymentDepositRequestDto;
import com.kijinkai.domain.payment.application.dto.WithdrawalRequestDto;
import com.kijinkai.domain.payment.domain.enums.PaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import com.kijinkai.domain.payment.domain.exception.PaymentAmountException;
import com.kijinkai.domain.payment.domain.exception.PaymentStatusException;
import com.kijinkai.domain.payment.domain.exception.PaymentTypeException;
import com.kijinkai.domain.payment.domain.repository.DepositRequestJpaRepository;
import com.kijinkai.domain.payment.domain.repository.DepositRequestRepository;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.entity.WalletStatus;
import com.kijinkai.domain.wallet.exception.WalletNotActiveException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class PaymentValidator {

    private DepositRequestRepository depositRequestRepository;

    public void requiredPendingStatus(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new PaymentStatusException("payment cannot be completed; it must be in pending status");
        }
    }

    public void requiredCompletedStatus(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentStatusException("payment cannot be refund; it must be in completed status");
        }
    }

    public void validateAmount(PaymentDepositRequestDto requestDto) {
        if (requestDto.getAmountOriginal().compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentAmountException("Amount must be a positive value");
        }
    }

    public void validateAmountByWithdrawal(WithdrawalRequestDto requestDto) {
        if (requestDto.getAmountOriginal().compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentAmountException("Amount must be a positive value");
        }
    }

    public void validateWithdrawalType(Payment payment) {
        if (!payment.getPaymentType().equals(PaymentType.WITHDRAWAL)) {
            throw new PaymentTypeException("거래 타입이 출금이 아닙니다.");
        }
    }

    public void validateDepositEligibility(BigDecimal originalAmount, Wallet wallet) {

        if (!wallet.getWalletStatus().equals(WalletStatus.ACTIVE)){
            throw new WalletNotActiveException("Inactive wallets can not be deposited");
        }

        if (originalAmount.compareTo(new BigDecimal("1000")) < 0) {
            throw new PaymentAmountException("The minimum deposit amount is 1,000 en");
        }

        if (originalAmount.compareTo(new BigDecimal("1000000")) > 0) {
            throw new PaymentAmountException("The maximum deposit amount is 1,000,000 en");
        }

    }

    public void validateWithdrawEligibility(BigDecimal originalAmount) {

        BigDecimal minimumAmount = new BigDecimal("20000");
        if (originalAmount.compareTo(minimumAmount) < 0) {
            throw new PaymentAmountException("The minimum withdraw is 20,000 en");
        }
    }

}
