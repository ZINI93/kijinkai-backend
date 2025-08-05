package com.kijinkai.domain.payment.domain.exception;

public class DepositRequestExpiredException extends RuntimeException {
    public DepositRequestExpiredException(String message) {
        super(message);
    }
}
