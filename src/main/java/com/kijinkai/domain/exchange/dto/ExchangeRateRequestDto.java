package com.kijinkai.domain.exchange.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ExchangeRateRequestDto {

    Currency currency;
    BigDecimal rate;

}
