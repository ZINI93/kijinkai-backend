package com.kijinkai.domain.delivery.exception;

public class DeliveryUpdateException extends RuntimeException {
    public DeliveryUpdateException(String message) {
        super(message);
    }
    public DeliveryUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
