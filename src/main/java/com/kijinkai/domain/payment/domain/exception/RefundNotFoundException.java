package com.kijinkai.domain.payment.domain.exception;

public class RefundNotFoundException extends RuntimeException {
    public RefundNotFoundException(String message) {
        super(message);
    }
}
