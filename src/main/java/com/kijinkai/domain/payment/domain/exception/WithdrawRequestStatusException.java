package com.kijinkai.domain.payment.domain.exception;

public class WithdrawRequestStatusException extends RuntimeException {
    public WithdrawRequestStatusException(String message) {
        super(message);
    }


    public WithdrawRequestStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
