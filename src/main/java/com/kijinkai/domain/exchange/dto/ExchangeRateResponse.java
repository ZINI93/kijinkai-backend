package com.kijinkai.domain.exchange.dto;


import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class ExchangeRateResponse {

    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal rate;
    private LocalDateTime fetchedAt;

    public ExchangeRateResponse(Currency fromCurrency, Currency toCurrency,
                                BigDecimal rate, LocalDateTime fetchedAt) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.fetchedAt = fetchedAt;
    }
}