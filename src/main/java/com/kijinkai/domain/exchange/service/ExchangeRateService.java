package com.kijinkai.domain.exchange.service;

import com.kijinkai.domain.orderitem.entity.Currency;

import java.math.BigDecimal;

public interface ExchangeRateService {
    BigDecimal getExchangeRate(Currency from, Currency to);
}
