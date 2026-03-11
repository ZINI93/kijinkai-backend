package com.kijinkai.domain.payment.domain.enums;

public enum DepositMethod {

    // 결론: 관리 주체와 정산 주기를 기준으로 분리합니다.
    VIRTUAL_ACCOUNT("가상계좌", SettlementType.DELAYED, true),
    BANK_TRANSFER("무통장입금", SettlementType.MANUAL, false),
    CREDIT_CARD("신용카드", SettlementType.IMMEDIATE, true),
    E_WALLET("간편결제", SettlementType.IMMEDIATE, true); // 카카오페이, 토스 등

    private final String description;
    private final SettlementType settlementType; // 정산 유형
    private final boolean isAutoConfirmed;      // 자동 승인 여부

    DepositMethod(String description, SettlementType settlementType, boolean isAutoConfirmed) {
        this.description = description;
        this.settlementType = settlementType;
        this.isAutoConfirmed = isAutoConfirmed;
    }
}
