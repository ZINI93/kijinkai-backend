package com.kijinkai.domain.delivery.domain.model;

public enum DeliveryStatus {
    PENDING("보류", "통합진행중"),
    SHIPPED("배송시작", "결제가 완료된 후 배송시작"),
    DELIVERED("배달완료", "구매자의 배달지에 배달완료"),
    CANCELLED("취소", "부득이한 경우 배달취소");  //취소

    private final String displayName;
    private final String description;

    DeliveryStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}


