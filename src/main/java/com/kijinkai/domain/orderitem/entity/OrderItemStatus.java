package com.kijinkai.domain.orderitem.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderItemStatus {
    PENDING("대기", "결제 대기상태"),
    COMPLETED("완료", "결제 완료"),
    CANCELLED("취소", "결제 완료 후 취소"),
    REJECTED("거절", "결제 대기 중 구매불가 상태");

    private final String displayName;
    private final String description;
}
