package com.kijinkai.domain.platform.exception;

public class PlatformNotFoundException extends RuntimeException {
    public PlatformNotFoundException(String message) {
        super(message);
    }
}
