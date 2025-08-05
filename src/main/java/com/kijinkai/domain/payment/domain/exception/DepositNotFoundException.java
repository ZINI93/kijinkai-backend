package com.kijinkai.domain.payment.domain.exception;

public class DepositNotFoundException extends RuntimeException {
    public DepositNotFoundException(String message) {
        super(message);
    }
}
