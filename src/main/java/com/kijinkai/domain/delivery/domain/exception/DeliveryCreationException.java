package com.kijinkai.domain.delivery.domain.exception;

public class DeliveryCreationException extends RuntimeException {
    public DeliveryCreationException(String message) {
        super(message);
    }
    public DeliveryCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
