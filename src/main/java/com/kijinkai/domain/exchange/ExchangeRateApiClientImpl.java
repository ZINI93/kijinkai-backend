package com.kijinkai.domain.exchange;

import com.kijinkai.domain.exchange.dto.ExchangeApiResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class ExchangeRateApiClientImpl implements ExchangeRateApiClient{

    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    public BigDecimal getRate(String from, String to) {
        String url = "https://api.exchangerate-api.com/v4/latest/" + from;
        ExchangeApiResponse response = restTemplate.getForObject(url, ExchangeApiResponse.class);
        return response.getRates().get(to);
    }
    }
