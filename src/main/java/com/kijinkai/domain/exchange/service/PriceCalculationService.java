package com.kijinkai.domain.exchange.service;

import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PriceCalculationService {

    private final ExchangeRateService exchangeRateService; // final로 변경하여 @RequiredArgsConstructor로 주입

    private static final BigDecimal PERCENT_DIVISOR = BigDecimal.valueOf(100); // 100을 상수로 정의

    /**
     * 구매대행 가격 계산 예시
     * @param jpyPrice 일본 엔화 기준 가격
     * @param targetCurrency 대상 통화 (예: KRW)
     * @param serviceFeePercent 서비스 수수료 (백분율, 예: 5 for 5%)
     * @return 총 가격
     */
    public BigDecimal calculateTotalPrice(BigDecimal jpyPrice, Currency targetCurrency, BigDecimal serviceFeePercent) {
        // Exchange 서비스에서 환율 가져와 금액 환전
        BigDecimal convertedPrice = exchangeRateService.convertAmount(jpyPrice, Currency.JPY, targetCurrency);

        // 수수료 계산
        BigDecimal serviceFee = convertedPrice.multiply(serviceFeePercent.divide(PERCENT_DIVISOR, 2, RoundingMode.HALF_UP)); // 소수점 처리

        // 총 가격
        return convertedPrice.add(serviceFee);
    }


    /**
     * 자국화페 -> 엔화로 환전
     * @param jpyPrice 일본 엔화 기준 가격
     * @param targetCurrency 대상 통화 (예: KRW)
     * @param serviceFeePercent 서비스 수수료 (백분율, 예: 5 for 5%)
     * @return 총 가격
     */
    public BigDecimal convertAndCalculateTotalInJpy(BigDecimal jpyPrice, Currency targetCurrency, BigDecimal serviceFeePercent) {
        // Exchange 서비스에서 환율 가져와 금액 환전
        BigDecimal convertedPrice = exchangeRateService.convertAmount(jpyPrice, targetCurrency, Currency.JPY);

        // 수수료 계산
        BigDecimal serviceFee = convertedPrice.multiply(serviceFeePercent.divide(PERCENT_DIVISOR, 2, RoundingMode.HALF_UP)); // 소수점 처리

        // 총 가격
        return convertedPrice.add(serviceFee);
    }

//    /**
//     * 엔화 -> 자국화페로 환전
//     * @param jpyPrice 일본 엔화 기준 가격
//     * @param serviceFeePercent 서비스 수수료 (백분율, 예: 5 for 5%)
//     * @param targetCurrency 대상 통화 (예: KRW)
//     * @return 총 가격
//     */
//    public BigDecimal convertAndCalculateTotalInLocalCurrency(BigDecimal amount, Currency targetCurrency, BigDecimal serviceFeePercent) {
//        // Exchange 서비스에서 환율 가져와 금액 환전
//        BigDecimal convertedPrice = exchangeRateService.convertAmount(amount, Currency.JPY, targetCurrency);
//
//        // 수수료 계산
//        BigDecimal serviceFee = convertedPrice.multiply(serviceFeePercent.divide(PERCENT_DIVISOR, 2, RoundingMode.HALF_UP)); // 소수점 처리
//
//        // 총 가격
//        return convertedPrice.subtract(serviceFee);
//    }

    /**
     * 엔화 -> 자국화페로 환전  ( 수수료는 엔화로 차감이 되었기 때문에 ) 순수 환전된 금액
     * @param jpyPrice 일본 엔화 기준 가격
     * @param serviceFeePercent 서비스 수수료 (백분율, 예: 5 for 5%)
     * @param targetCurrency 대상 통화 (예: KRW)
     * @return 총 가격
     */
    public BigDecimal convertAndCalculateTotalInLocalCurrency(BigDecimal amount, Currency targetCurrency) {
        // Exchange 서비스에서 환율 가져와 금액 환전
        return exchangeRateService.convertAmount(amount, Currency.JPY, targetCurrency);
    }


    /**
     * 단순 환율 조회 예시 (JPY to KRW)
     */
    public BigDecimal getJpyToKrwRate() {
        return exchangeRateService.getExchangeRate(Currency.JPY, Currency.KRW);
    }
}