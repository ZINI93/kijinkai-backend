package com.kijinkai.domain.transaction.entity;

public enum TransactionStatus {

    REQUEST("요청", "관리자로 부터 요청 대기중"),
    COMPLETED("완료", "관리자가 확인 후 승인상태"),
    FAILED("실패", "실패"),
    CANCEL("취소", "관리자,유저가 무언가의 이유로 캔슬처리");


    private final String displayName;
    private final String descipriton;

    TransactionStatus(String displayName, String descipriton) {
        this.displayName = displayName;
        this.descipriton = descipriton;
    }
}
