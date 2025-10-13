package com.kijinkai.domain.orderitem.domain.exception;

public class OrderItemNotFoundException extends RuntimeException {
    public OrderItemNotFoundException(String message) {
        super(message);
    }

    public OrderItemNotFoundException() {

    }
}
