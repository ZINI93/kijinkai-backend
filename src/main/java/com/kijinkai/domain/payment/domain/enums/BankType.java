package com.kijinkai.domain.payment.domain.enums;

public enum BankType {
    KOOKMIN("국민은행"),
    NONGHYUP("농협"),
    WOORI("우리은행"),
    SHINHAN("신한은행"),
    HANA("하나은행"),
    KAKAO("카카오뱅크");

    private final String description;

    BankType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
