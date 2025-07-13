package com.kijinkai.domain.wallet.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WalletStatus {

    ACTIVE("활성", "정상적으로 사용 가능"),
    FROZEN("동결", "사용 중단"),
    CLOSED("폐쇄", "영구 사용불가");


    private final String displayName;
    private final String description;


}
