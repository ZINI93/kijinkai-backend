package com.kijinkai.domain.payment.domain.exception;

public class WithdrawApprovalException extends RuntimeException {
    public WithdrawApprovalException(String message) {
        super(message);
    }

    public WithdrawApprovalException(String message, Throwable cause) {
        super(message, cause);
    }
}
