package com.kijinkai.domain.payment.exception;

public class PaymentStatusException extends RuntimeException {
    public PaymentStatusException(String message) {
        super(message);
    }
}
