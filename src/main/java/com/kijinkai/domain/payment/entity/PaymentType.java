package com.kijinkai.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum PaymentType {
    DEPOSIT("입금"), //입금
    WITHDRAWAL("출금"),
    REFUND("환불");

    private final String displayName;

}
