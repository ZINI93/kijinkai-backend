package com.kijinkai.domain.campaign.domain.modal;

public enum RewardType {
    NONE,           // 보상 없음 (단순 공지나 정보 전달형 캠페인)
    POINT,          // 자체 포인트 (예: 500P 적립)
    COUPON,         // 할인 또는 무료 이용 쿠폰
    GIFTICON,       // 기프티콘 (외부 상품권)
    BADGE,          // 디지털 배지 (프로필 전시용, 게이미피케이션 요소)
    EXPERIENCE,     // 경험치 (레벨업 시스템이 있는 경우)
    PHYSICAL_GIFT,  // 실물 경품 (추첨을 통해 배송되는 상품)
    DISCOUNT_CODE   // 프로모션 코드 (결제 시 직접 입력하는 코드)
}
