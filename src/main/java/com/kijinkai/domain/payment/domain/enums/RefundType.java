package com.kijinkai.domain.payment.domain.enums;

public enum RefundType {

    STOCK_OUT,                 // 재고 부족
    PURCHASE_CANCELLED,        // 구매 취소
    DEFECTIVE_PRODUCT,         // 상품 불량
    ADMIN_DECISION       // 관리자 판단
}
