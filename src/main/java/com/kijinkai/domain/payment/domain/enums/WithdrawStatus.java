package com.kijinkai.domain.payment.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WithdrawStatus {

    PENDING_ADMIN_APPROVAL("관리자 승인 대기", "승인 대기"),
    APPROVED("승인됨", "지가 차감 완료"),
    BANK_TRANSFER_PENDING("실제 송금대기", "송금대기"),
    COMPLETED("송금 완료", "유저 계좌에 송금 완료"),
    REJECTED("거절", "거절"),
    FAILED("처리 실패", "처리 실패");



    private final String displayName;
    private final String description;// 처리 실패

}
