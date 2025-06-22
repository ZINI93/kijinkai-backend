package com.kijinkai.domain.exchange.repository;

import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface ExchangeRateRepository {
  ExchangeRate save(ExchangeRate exchangeRate);
  Optional<ExchangeRate> findLatestExchangeRate(String fromCurrency, String toCurrency);
  // 필요한 경우 추가적인 조회 메서드를 정의할 수 있습니다.
  // List<ExchangeRate> findByFromAndToCurrencyAndTimeRange(String from, String to, LocalDateTime start, LocalDateTime end);
}