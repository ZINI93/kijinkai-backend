package com.kijinkai.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    PENDING("대기", "처리 대기 중"),
    COMPLETED("완료", "처리 완료"),
    CANCELLED("취소", "결제 취소됨"),
    REJECTED("거절", "요청 거절됨"),
    EXPIRED("만료", "유효 시간 만료");


    private final String displayName;
    private final String description;

    public boolean isTerminalStatus() {
        return this == COMPLETED || this == CANCELLED || this == REJECTED || this == EXPIRED;
    }

    public boolean canTransitionTo(PaymentStatus targetStatus) {
        return switch (this) {
            case PENDING -> targetStatus == COMPLETED || targetStatus == CANCELLED ||
                    targetStatus == REJECTED || targetStatus == EXPIRED;
            case COMPLETED, CANCELLED, REJECTED, EXPIRED -> false;
        };
    }
}

