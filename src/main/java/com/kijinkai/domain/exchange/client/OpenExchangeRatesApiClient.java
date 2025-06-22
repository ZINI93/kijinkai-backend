package com.kijinkai.domain.exchange.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kijinkai.domain.exchange.exception.ExchangeRateApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component // 또는 @Service (인프라 서비스도 Component의 일종)
@RequiredArgsConstructor
@Slf4j
public class OpenExchangeRatesApiClient implements ExchangeRateApiClient {

    @Value("${exchangerate.api.url}")
    private String apiUrl;
    @Value("${exchangerate.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate; // Spring RestTemplate 또는 WebClient 사용
    private final ObjectMapper objectMapper; // JSON 파싱용

    @Override
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) throws ExchangeRateApiException {
        // 실제 API 엔드포인트와 파라미터는 사용하는 API에 따라 달라집니다.
        // 여기서는 예시로 Open Exchange Rates의 "latest" 엔드포인트를 가정합니다.
        // https://openexchangerates.org/api/latest.json?app_id=YOUR_APP_ID
        // 이 API는 USD를 기준으로 다른 통화의 환율을 제공하므로, 통화 변환 로직이 필요할 수 있습니다.
        // 예를 들어 USD -> KRW는 직접 제공되지만, EUR -> KRW는 EUR -> USD -> KRW 변환이 필요할 수 있습니다.

        String url = String.format("%s/latest.json?app_id=%s&base=%s&symbols=%s",
                apiUrl, apiKey, fromCurrency.toUpperCase(), toCurrency.toUpperCase());

        try {
            log.info("Calling exchange rate API: {}", url);
            String response = restTemplate.getForObject(url, String.class);

            if (response == null) {
                throw new ExchangeRateApiException("Empty response from exchange rate API.");
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode ratesNode = root.path("rates");
            JsonNode targetRateNode = ratesNode.path(toCurrency.toUpperCase());

            if (targetRateNode.isMissingNode() || !targetRateNode.isNumber()) {
                throw new ExchangeRateApiException("Invalid or missing rate for " + toCurrency.toUpperCase() + " in API response: " + response);
            }

            return targetRateNode.decimalValue();

        } catch (Exception e) {
            log.error("Error calling exchange rate API for {} to {}: {}", fromCurrency, toCurrency, e.getMessage());
            throw new ExchangeRateApiException("Failed to fetch exchange rate from API: " + e.getMessage(), e);
        }
    }
}