package com.kijinkai.domain.payment.validate;

import com.kijinkai.domain.payment.dto.PaymentDepositRequestDto;
import com.kijinkai.domain.payment.dto.WithdrawalRequestDto;
import com.kijinkai.domain.payment.entity.Payment;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import com.kijinkai.domain.payment.entity.PaymentType;
import com.kijinkai.domain.payment.exception.PaymentAmountException;
import com.kijinkai.domain.payment.exception.PaymentStatusException;
import com.kijinkai.domain.payment.exception.PaymentTypeException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentValidator {

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

    public void validateAmount(PaymentDepositRequestDto requestDto){
        if (requestDto.getAmountOriginal().compareTo(BigDecimal.ZERO) < 0){
            throw new PaymentAmountException("Amount must be a positive value");
        }
    }

    public void validateAmountByWithdrawal(WithdrawalRequestDto requestDto){
        if (requestDto.getAmountOriginal().compareTo(BigDecimal.ZERO) < 0){
            throw new PaymentAmountException("Amount must be a positive value");
        }
    }

    public void validateWithdrawalType(Payment payment){
        if (!payment.getPaymentType().equals(PaymentType.WITHDRAWAL)){
            throw new PaymentTypeException("거래 타입이 출금이 아닙니다.");
        }
    }



}
