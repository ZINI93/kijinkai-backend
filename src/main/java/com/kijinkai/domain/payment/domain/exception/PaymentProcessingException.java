package com.kijinkai.domain.payment.domain.exception;

public class PaymentProcessingException extends RuntimeException {
    public PaymentProcessingException(String message) {
        super(message);
    }
    public PaymentProcessingException(String message, Throwable cases) {
        super(message, cases);
    }
}
