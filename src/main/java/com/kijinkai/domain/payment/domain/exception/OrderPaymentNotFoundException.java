package com.kijinkai.domain.payment.domain.exception;

public class OrderPaymentNotFoundException extends RuntimeException {
    public OrderPaymentNotFoundException(String message) {
        super(message);
    }
}
