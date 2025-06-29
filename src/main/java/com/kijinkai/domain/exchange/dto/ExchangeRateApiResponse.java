package com.kijinkai.domain.exchange.dto;

import com.google.gson.annotations.SerializedName;
import com.kijinkai.domain.exchange.doamin.Currency;

import java.util.Map;

public class ExchangeRateApiResponse {

    @SerializedName("base")
    private String baseCurrency; // 외부 API는 String으로 제공할 수 있으므로 String 유지

    @SerializedName("date")
    private String date;

    @SerializedName("rates")
    private Map<String, Double> rates;

    @SerializedName("success")
    private boolean success;

    // 생성자 (Lombok 적용했다면 @NoArgsConstructor, @AllArgsConstructor 등으로 대체 가능)
    public ExchangeRateApiResponse() {}

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Map<String, Double> getRates() { return rates; }
    public void setRates(Map<String, Double> rates) { this.rates = rates; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    /**
     * 특정 통화의 환율 조회 (String 코드 사용)
     */
    public Double getRate(String currencyCode) {
        return rates != null ? rates.get(currencyCode) : null;
    }

    /**
     * 특정 통화의 환율 조회 (Currency Enum 사용)
     */
    public Double getRate(Currency currency) {
        return getRate(currency.name()); // Enum의 이름을 String으로 변환하여 사용
    }
}