package com.kijinkai.domain.user.domain.exception;

public class AlreadyVerifiedException extends RuntimeException {
    public AlreadyVerifiedException(String message) {
        super(message);
    }
}
