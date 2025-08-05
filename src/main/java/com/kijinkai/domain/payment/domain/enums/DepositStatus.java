package com.kijinkai.domain.payment.domain.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DepositStatus {

        PENDING_ADMIN_APPROVAL("관리자 승인 대기", "승인대기"),
        APPROVED("승인됨","지갑에 돈이 충전"),
        REJECTED("거절됨","거절"),
        EXPIRED("만료됨","유효기간 초과로 만료");

    private final String displayName;
    private final String description;
}
