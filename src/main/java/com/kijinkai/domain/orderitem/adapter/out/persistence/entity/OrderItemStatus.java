package com.kijinkai.domain.orderitem.adapter.out.persistence.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderItemStatus {

    // 구매요청 -> 구매승인 -> 1차결제 완료 -> 현지배송 완료 -> 통합진행중 -> 2차결제 요청 -> 2차결제 완료 -> 국제배송 -> 배송완료


    PENDING("구매요청", "구매요청하는 단계"),

    PENDING_APPROVAL("관리자 상품 체크 완료", "구매요청된 상품을 승인"),

    PRODUCT_PAYMENT_COMPLETED("상품결제완료", "상품에 대한 1차 결제 완료"),

    LOCAL_DELIVERY_COMPLETED("주문된 상품 도착", "관리자가 주문한 상품이 현지에 도착"),

    PRODUCT_CONSOLIDATING("통합진행", "포장/계측 등 상품에 대한 포장"),

    DELIVERY_FEE_PAYMENT_REQUEST("배송비 결제 요청", "주문된 상품의 사이즈를 바탕으로 2차 결제 요청"),

    DELIVERY_FEE_PAYMENT_COMPLETED("배송비결제완료","상품 대한 배송비 결제"),

    IN_TRANSIT("국제 배송", "국제 배송중"),

    DELIVERED("배송 완료", "고객이 물건을 받고 배송완료"),




    //PRODUCT_PURCHASE("상품을 주문", "관리자가 결제가 완료된 상품을 실제 주문 하는 단계"),

    //구매처리
    COMPLETED("완료", "결제 완료"),
    CANCELLED("취소", "결제 완료 후 취소"),
    REJECTED("거절", "결제 대기 중 구매불가 상태");

    private final String displayName;
    private final String description;

}
