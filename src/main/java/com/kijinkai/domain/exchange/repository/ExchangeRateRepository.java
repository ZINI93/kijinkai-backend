package com.kijinkai.domain.exchange.repository;

import com.kijinkai.domain.orderitem.entity.Currency;
import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

  Optional<ExchangeRate> findTopByFromCurrencyAndToCurrencyOrderByFetchedAtDesc(Currency from, Currency to);
}
