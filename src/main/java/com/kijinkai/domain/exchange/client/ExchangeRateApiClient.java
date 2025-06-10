package com.kijinkai.domain.exchange.client;

import com.kijinkai.domain.exchange.dto.ExchangeApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ExchangeRateApiClient {

    private final RestTemplate restTemplate;

    public ExchangeApiResponse getExchangeRates(String baseCurrency) {
        String url = "https://api.exchangerate-api.com/v4/latest/" + baseCurrency;
        return restTemplate.getForObject(url, ExchangeApiResponse.class);
    }
}
