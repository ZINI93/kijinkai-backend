package com.kijinkai.domain.customer.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException(UUID customerUuid) {
        super("고객을 찾을 수 없습니다. UUID:" + customerUuid);
    }
}
