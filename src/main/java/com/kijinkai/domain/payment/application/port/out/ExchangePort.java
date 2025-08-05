package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.exchange.doamin.Currency;

import java.math.BigDecimal;

public interface ExchangePort {

    BigDecimal exchangeRate(Currency originalCurrency, Currency targetCurrency);

}
