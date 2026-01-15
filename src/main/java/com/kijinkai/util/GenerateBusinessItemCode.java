package com.kijinkai.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class GenerateBusinessItemCode {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");


    public String generateBusinessCode(String userUuid, BusinessCodeType codeType){

        // 날짜 기반 접두어
        String datePart = LocalDate.now().format(DATE_FORMATTER);

        // 유저 식별값 일부
        String userPart = (userUuid != null && userUuid.length() > 4)
                ? userUuid.substring(userUuid.length() - 4)
                : "0000";

        // 예측 불가능한 랜덤타입
        int randomPart = RANDOM.nextInt(900) + 100; // 100 - 999사이

        return String.format(codeType + "-%s-%s-%d", datePart, userPart, randomPart );
    }
}
