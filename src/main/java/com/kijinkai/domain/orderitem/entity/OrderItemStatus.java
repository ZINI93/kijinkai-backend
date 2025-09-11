package com.kijinkai.domain.orderitem.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderItemStatus {

    //기본 상태
    PENDING("구매요청", "결제 대기상태"),
    PENDING_APPROVAL("관리자 상품 체크 완료", "관리자가 상품 결제 체크하는 단계"),

    //관리자
    PRODUCT_PURCHASE("상품을 주문", "관리자가 결제가 완료된 상품을 실제 주문 하는 단계"),
    PRODUCT_PURCHASE_COMPLETE("주문된 상품 도착", "관리자가 주문한 상품이 도착"),


    //결제 관련
    PRODUCT_PAYMENT_COMPLETED("상품결제완료", "상품에 대한 1차 결제"),
    DELIVERY_FEE_PAYMENT_REQUEST("배송비 결제 요청", "주문된 상품의 사이즈를 바탕으로 배송비 결제 요청"),
    DELIVERY_FEE_PAYMENT_COMPLETED("배송비결제완료","상품 대한 배송비 결제"),

    //구매처리
    COMPLETED("완료", "결제 완료"),
    CANCELLED("취소", "결제 완료 후 취소"),
    REJECTED("거절", "결제 대기 중 구매불가 상태");

    private final String displayName;
    private final String description;

}
