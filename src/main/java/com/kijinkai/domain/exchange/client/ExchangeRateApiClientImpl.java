package com.kijinkai.domain.exchange.client;

import com.kijinkai.domain.exchange.exception.ExchangeRateApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class ExchangeRateApiClientImpl implements ExchangeRateApiClient{

    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) throws ExchangeRateApiException {
        return null;
    }
}
