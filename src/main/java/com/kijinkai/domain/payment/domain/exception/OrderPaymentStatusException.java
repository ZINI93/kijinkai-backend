package com.kijinkai.domain.payment.domain.exception;

public class OrderPaymentStatusException extends RuntimeException {
    public OrderPaymentStatusException(String message) {
        super(message);
    }
    public OrderPaymentStatusException(String message, Throwable cause) {
        super(message, cause);
    }


}
