package com.kijinkai.domain.exchange.client;

import com.kijinkai.domain.exchange.exception.ExchangeRateApiException;

import java.math.BigDecimal;

public interface ExchangeRateApiClient {
    /**
     * 외부 환율 API로부터 특정 통화 쌍의 현재 환율을 조회합니다.
     * @param fromCurrency 기준 통화
     * @param toCurrency 대상 통화
     * @return 조회된 환율 값
     * @throws ExchangeRateApiException API 호출 실패 시
     */
    BigDecimal getExchangeRate(String fromCurrency, String toCurrency) throws ExchangeRateApiException;
}