package com.kijinkai.util;

public enum BusinessCodeType {

    ORI("ORI", "주문 상품 식별번호" ),
    ORD("ORD","주문 식별 번호" ); // 주문코드

    private final String prefix;
    private final String description;

    BusinessCodeType(String prefix, String description) {
        this.prefix = prefix;
        this.description = description;
    }

    public String getPrefix() {
        return prefix;
    }
}
