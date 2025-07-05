package com.kijinkai.domain.user.exception;

public class InvalidUserStatusException extends RuntimeException {
    public InvalidUserStatusException(String message) {
        super(message);
    }
}
