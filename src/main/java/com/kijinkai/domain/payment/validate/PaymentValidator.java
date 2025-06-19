package com.kijinkai.domain.payment.validate;

import com.kijinkai.domain.payment.dto.PaymentRequestDto;
import com.kijinkai.domain.payment.entity.Payment;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import com.kijinkai.domain.payment.exception.PaymentAmountException;
import com.kijinkai.domain.payment.exception.PaymentStatusException;
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

    public void validateAmount(PaymentRequestDto requestDto){
        if (requestDto.getAmountOriginal().compareTo(BigDecimal.ZERO) < 0){
            throw new PaymentAmountException("Amount must be a positive value");
        }
    }



}
