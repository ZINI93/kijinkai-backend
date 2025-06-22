package com.kijinkai.domain.exchange.service;

import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.orderitem.entity.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeRateService {
    /**
     * 외부 API로부터 새로운 환율 정보를 가져와 저장합니다.
     * @param fromCurrency 기준 통화
     * @param toCurrency 대상 통화
     * @return 저장된 ExchangeRate 엔티티
     */
    ExchangeRate fetchAndSaveExchangeRate(Currency fromCurrency, Currency toCurrency);

    /**
     * 특정 통화 쌍의 최신 환율 정보를 조회합니다.
     * @param fromCurrency 기준 통화
     * @param toCurrency 대상 통화
     * @return 최신 ExchangeRate (Optional)
     */
    Optional<ExchangeRate> getLatestExchangeRate(Currency fromCurrency, Currency toCurrency);
}
