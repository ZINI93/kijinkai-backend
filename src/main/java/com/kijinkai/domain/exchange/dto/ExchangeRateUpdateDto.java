package com.kijinkai.domain.exchange.dto;


import lombok.Value;

import java.math.BigDecimal;

@Value
public class ExchangeRateUpdateDto {

    BigDecimal exchangeRate;

}
