package com.kijinkai.domain.payment.domain.enums;

public enum SettlementType {
    IMMEDIATE("즉시 확정"),  // PG사/카드사 승인 즉시 매출로 잡힘
    DELAYED("지연 확정"),    // 가상계좌 등 입금 통지(Webhook)를 받아야 함
    MANUAL("수동 확인"),     // 무통장 입금처럼 사람이 직접 확인해야 함
    INTERNAL("내부 정산");    // 포인트, 쿠폰 등 외부 결제 수단이 아닌 경

    private final String description;

    SettlementType(String description) {
        this.description = description;
    }
}
