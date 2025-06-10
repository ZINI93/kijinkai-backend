package com.kijinkai.domain.orderitem.exception;

public class OrderItemInvalidException extends RuntimeException {
    public OrderItemInvalidException(String message) {
        super(message);
    }
}
