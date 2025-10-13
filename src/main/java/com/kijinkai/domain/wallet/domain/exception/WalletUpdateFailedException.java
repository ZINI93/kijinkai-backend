package com.kijinkai.domain.wallet.domain.exception;

public class WalletUpdateFailedException extends RuntimeException {
    public WalletUpdateFailedException(String message) {
        super(message);
    }
}
