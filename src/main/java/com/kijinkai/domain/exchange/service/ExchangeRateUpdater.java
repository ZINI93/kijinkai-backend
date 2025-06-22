package com.kijinkai.domain.exchange.service;

import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateUpdater {

    private final ExchangeRateService exchangeRateService; // 도메인 서비스 의존

    /**
     * 지정된 통화 쌍의 환율을 외부 API로부터 가져와 업데이트합니다.
     * 이 메서드는 트랜잭션을 관리하며, 비즈니스 유스케이스를 나타냅니다.
     *
     * @param fromCurrency 기준 통화
     * @param toCurrency 대상 통화
     * @return 업데이트된 ExchangeRate 엔티티
     */
    @Transactional
    public ExchangeRate updateExchangeRate(String fromCurrency, String toCurrency) {
        log.info("Updating exchange rate for {} to {}", fromCurrency, toCurrency);
        try {
            ExchangeRate updatedRate = exchangeRateService.fetchAndSaveExchangeRate(fromCurrency, toCurrency);
            log.info("Successfully updated exchange rate: {} {} -> {} {}",
                    updatedRate.getRate(), updatedRate.getFromCurrency(), updatedRate.getToCurrency(), updatedRate.getFetchedAt());
            return updatedRate;
        } catch (Exception e) {
            log.error("Failed to update exchange rate for {} to {}: {}", fromCurrency, toCurrency, e.getMessage());
            // 예외 처리 전략: 특정 비즈니스 예외로 래핑하거나, 로깅 후 재던지기 등
            throw new RuntimeException("Exchange rate update failed", e);
        }
    }

    // 다른 유스케이스 (예: 특정 시점 환율 조회 등)가 있다면 여기에 추가 가능
}
