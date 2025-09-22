package com.kijinkai.domain.user.domain.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String message) {
        super(message);
    }
    public EmailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
