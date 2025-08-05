package com.kijinkai.domain.payment.domain.exception;

public class WithdrawRequestNotFoundException extends RuntimeException {
    public WithdrawRequestNotFoundException(String message) {
        super(message);
    }
}
