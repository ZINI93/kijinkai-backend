package com.kijinkai.domain.payment.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentType {
    DEPOSIT("지갑 예금"), //입금
    WITHDRAWAL("출금"),
    REFUND("환불"),
    PRODUCT_PAYMENT("상품 가격 지불"),
    SHIPPING_PAYMENT("배송비 지불");



    private final String displayName;

}
