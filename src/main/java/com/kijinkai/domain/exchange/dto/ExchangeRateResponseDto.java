package com.kijinkai.domain.exchange.dto;


import com.kijinkai.domain.exchange.doamin.Currency;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Value
@Builder
public class ExchangeRateResponseDto {

    Currency currency;
    BigDecimal rate;
    LocalDateTime fetchedAt;

    public ExchangeRateResponseDto(Currency currency,
                                   BigDecimal rate, LocalDateTime fetchedAt) {

        this.currency = currency;
        this.rate = rate;
        this.fetchedAt = fetchedAt;
    }
}