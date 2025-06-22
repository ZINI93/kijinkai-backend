package com.kijinkai.domain.exchange.service;

import com.kijinkai.domain.exchange.client.ExchangeRateApiClient;
import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.exchange.exception.ExchangeRateApiException;
import com.kijinkai.domain.exchange.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service // Spring Bean으로 등록
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository; // 도메인 리포지토리 의존
    private final ExchangeRateApiClient exchangeRateApiClient;   // 인프라 계층의 API 클라이언트 의존

    @Override
    public ExchangeRate fetchAndSaveExchangeRate(String fromCurrency, String toCurrency) {
        BigDecimal currentRate;
        try {
            currentRate = exchangeRateApiClient.getExchangeRate(fromCurrency, toCurrency);
        } catch (ExchangeRateApiException e) {
            log.error("Failed to fetch exchange rate from API for {} to {}: {}", fromCurrency, toCurrency, e.getMessage());
            // 비즈니스적으로 중요한 예외라면, 도메인 예외로 전환하여 상위 계층으로 던질 수 있습니다.
            // throw new ExchangeRateDomainException("Failed to get current exchange rate", e);
            throw new RuntimeException("Failed to get current exchange rate", e); // 현재는 런타임 예외로 처리
        }

        ExchangeRate newExchangeRate = ExchangeRate.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .rate(currentRate)
                .fetchedAt(LocalDateTime.now())
                .build();

        return exchangeRateRepository.save(newExchangeRate);
    }

    @Override
    public Optional<ExchangeRate> getLatestExchangeRate(String fromCurrency, String toCurrency) {
        return exchangeRateRepository.findLatestExchangeRate(fromCurrency, toCurrency);
    }
}
