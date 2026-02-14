package com.kijinkai.domain.coupon.domain.exception;

import lombok.Getter;

@Getter
public class CouponValidateException extends RuntimeException {

    private final String errorCode;

    public CouponValidateException(CouponErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.code;
    }


    public CouponValidateException(CouponErrorCode errorCode, Object... args) {
        super(errorCode.getFormattedMessage(args));
        this.errorCode = errorCode.getCode();
    }
}
