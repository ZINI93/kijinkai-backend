package com.kijinkai.domain.order.entity;

public enum OrderStatus {
    DRAFT,               // 유저가 링크만 작성
    PENDING_APPROVAL,    // 관리자가 가격 정보 입력 등 체크
    AWAITING_PAYMENT,    // 유저 결제 대기 중
    CANCEL,              // 유저가 취소 (결제 완료 후 취소 불가능)
    FIRST_PAID,          // 1차 결제완료 결제 완료
    SECOND_PAID,         // 2차 결제완료 결제 완료
    PREPARE_DELIVERY,    // 배송준비중
    SHIPPING,            // 배송 중
    DELIVERED,           // 고객 수령 완료
    REJECTED             // 관리자 거절
}
