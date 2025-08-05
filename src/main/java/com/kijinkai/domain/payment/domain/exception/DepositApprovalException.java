package com.kijinkai.domain.payment.domain.exception;

public class DepositApprovalException extends RuntimeException {
    public DepositApprovalException(String message) {
        super(message);
    }

    public DepositApprovalException(String message, Throwable cases ) {
        super(message, cases);
    }
}
