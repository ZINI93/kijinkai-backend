package com.kijinkai.domain.payment.domain.exception;

public class DepositRequestNotFoundException extends RuntimeException {
    public DepositRequestNotFoundException(String message) {
        super(message);
    }
}
