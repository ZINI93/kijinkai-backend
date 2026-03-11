package com.kijinkai.domain.delivery.domain.model;

public enum DeliveryStatus {
    PENDING("보류", "요청을 받은 상태"),
    PACKED("배송준비완료", "상품 통합 후 배송준비 완료"),
    REQUEST_PAYMENT("배송비지불요청", "무게 측정 후 배송비 결제 대기"),
    PAID("배송비 지불", "배송비 지불이 완료된 배송"),
    SHIPPED("배송중", "결제가 완료된 후 배송시작"),
    DELIVERED("배달완료", "구매자의 배달지에 배달완료"),
    CANCELLED("취소", "부득이한 경우 배달취소");

    private final String displayName;
    private final String description;

    DeliveryStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}


