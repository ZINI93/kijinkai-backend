package com.kijinkai.domain.coupon.application.util;

import com.kijinkai.domain.coupon.domain.exception.CouponCodeGenerateException;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CouponCodeGenerator {

    private static final String CHAR_SET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();


    /**
     * @param length 생성할 쿠폰 번호의 길이 (예: 12자리)
     * @param splitEvery 하이픈(-)으로 나눌 단위 (예: 4자리마다)
     * @return 예: ABCD-EFGH-JKLM
     */
    public static String generate(int length, int splitEvery) {
        String code = IntStream.range(0, length)
                .mapToObj(i -> String.valueOf(CHAR_SET.charAt(RANDOM.nextInt(CHAR_SET.length()))))
                .collect(Collectors.joining());


        if (splitEvery <= 0) return code;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            if (i > 0 && i % splitEvery == 0) {
                sb.append("-");
            }
            sb.append(code.charAt(i));
        }
        return sb.toString();
    }


    public static String generateDefault(){
        return generate(12,4);
    }




}
