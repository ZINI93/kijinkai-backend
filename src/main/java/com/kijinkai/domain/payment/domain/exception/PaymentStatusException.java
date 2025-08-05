package com.kijinkai.domain.payment.domain.exception;

public class PaymentStatusException extends RuntimeException {
    public PaymentStatusException(String message) {
        super(message);
    }
}
