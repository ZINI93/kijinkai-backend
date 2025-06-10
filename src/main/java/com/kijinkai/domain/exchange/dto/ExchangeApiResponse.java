package com.kijinkai.domain.exchange.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExchangeApiResponse {
    private String base;
    private Map<String, BigDecimal> rates;
    private String date;
}


