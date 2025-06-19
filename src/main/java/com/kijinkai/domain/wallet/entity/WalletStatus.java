package com.kijinkai.domain.wallet.entity;

public enum WalletStatus {

    ACTIVE, //정상적으로 사용 가능
    INACTIVE,  //(고객 요청 등으로) 일시적으로 사용 중지
    FROZEN, //(이상 거래 감지 등으로) 동결 상태, 입출금 불가
    CLOSED, //월렛이 폐쇄됨
}
