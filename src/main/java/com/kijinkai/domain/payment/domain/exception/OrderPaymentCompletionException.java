package com.kijinkai.domain.payment.domain.exception;

public class OrderPaymentCompletionException extends RuntimeException {
    public OrderPaymentCompletionException(String message) {
        super(message);
    }

    public OrderPaymentCompletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
