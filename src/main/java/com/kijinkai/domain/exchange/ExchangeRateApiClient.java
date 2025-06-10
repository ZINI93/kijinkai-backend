package com.kijinkai.domain.exchange;

import java.math.BigDecimal;

public interface ExchangeRateApiClient {
    BigDecimal getRate(String from, String to);
}
