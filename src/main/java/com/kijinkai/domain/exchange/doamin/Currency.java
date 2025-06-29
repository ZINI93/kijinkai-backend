package com.kijinkai.domain.exchange.doamin;

import java.util.Arrays;
import java.util.InputMismatchException;

public enum Currency {
    JPY("JPY", "일본 엔"),
    KRW("KRW", "한국 원"),
    CLP("PHP", "칠레 페소"),
    USD("USD", "미국 달러");

    private final String code;
    private final String name;

    Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 유효한 통화 코드인지 확인
     */
    public static boolean isValidCode(String code) {
        return Arrays.stream(values())
                .anyMatch(currency -> currency.name().equals(code)); // name()은 Enum 상수 이름 반환
    }

    /**
     * 코드로 Currency Enum 찾기 (대소문자 구분)
     * 유효하지 않은 코드의 경우 IllegalArgumentException 대신 InputMismatchException을 던짐
     */
    public static Currency fromCode(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("통화 코드가 비어있을 수 없습니다.");
        }
        try {
            return Currency.valueOf(code.toUpperCase()); // Enum.valueOf() 사용, 대문자로 변환하여 일치시킴
        } catch (IllegalArgumentException e) {
            throw new InputMismatchException("지원하지 않는 통화 코드입니다: " + code);
        }
    }
}