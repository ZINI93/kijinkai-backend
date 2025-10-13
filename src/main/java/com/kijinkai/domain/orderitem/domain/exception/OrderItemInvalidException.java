package com.kijinkai.domain.orderitem.domain.exception;

public class OrderItemInvalidException extends RuntimeException {
    public OrderItemInvalidException(String message) {
        super(message);
    }
}
