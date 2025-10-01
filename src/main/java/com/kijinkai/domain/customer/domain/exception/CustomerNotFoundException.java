package com.kijinkai.domain.customer.domain.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    private final String errorCode;
    private final String resourceId;

    public CustomerNotFoundException(String message, String errorCode, String resourceId) {
        super(message);
        this.errorCode = errorCode;
        this.resourceId = resourceId;
    }

    public CustomerNotFoundException(String message) {
        this(message, "CUSTOMER_NOT_FOUND", null);
    }

    public CustomerNotFoundException(UUID customerUuid) {
        this("고객을 찾을 수 없습니다. UUID:" + customerUuid,
                "CUSTOMER_NOT_FOUND",
                customerUuid.toString());
    }

    public String getErrorCode() { return errorCode; }
    public String getResourceId() { return resourceId; }
}