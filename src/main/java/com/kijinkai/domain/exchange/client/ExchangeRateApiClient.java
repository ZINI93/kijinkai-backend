package com.kijinkai.domain.exchange.client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kijinkai.domain.exchange.dto.ExchangeRateApiResponse;
import com.kijinkai.domain.exchange.util.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateApiClient {

    private final HttpClientUtil httpClientUtil;

    private final Gson gson = new Gson();

    @Value("${spring.api.exchange-rate.url}") // application.properties에서 URL 주입받음
    private String exchangeRateApiUrl;

    /**
     * 외부 API에서 환율 조회
     */
    public BigDecimal fetchExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            String url = String.format("%s%s", exchangeRateApiUrl, baseCurrency);
            log.debug("외부 환율 API 호출: {}", url); // 디버그 로그

            String jsonResponse = httpClientUtil.get(url);
            log.trace("외부 환율 API 응답: {}", jsonResponse); // 상세 응답 로깅

            ExchangeRateApiResponse apiResponse = gson.fromJson(jsonResponse, ExchangeRateApiResponse.class);

            if (!apiResponse.isSuccess()) {
                throw new RuntimeException("외부 환율 API 응답 실패: success=false");
            }

            Double rate = apiResponse.getRate(targetCurrency);
            if (rate == null) {
                throw new RuntimeException("외부 환율 API 응답에서 환율 정보를 찾을 수 없습니다: " + targetCurrency);
            }

            return BigDecimal.valueOf(rate);

        } catch (IOException e) { // HTTP 통신 관련 예외
            log.error("환율 API HTTP 통신 실패: {}", e.getMessage(), e);
            throw new RuntimeException("환율 API HTTP 통신 실패", e);
        } catch (InterruptedException e) { // 스레드 인터럽트 예외
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
            log.error("환율 API 호출 중 스레드 인터럽트 발생: {}", e.getMessage(), e);
            throw new RuntimeException("환율 API 호출 중 스레드 인터럽트 발생", e);
        } catch (JsonSyntaxException e) { // JSON 파싱 예외
            log.error("환율 API 응답 JSON 파싱 실패: {}", e.getMessage(), e);
            throw new RuntimeException("환율 API 응답 JSON 파싱 실패", e);
        } catch (Exception e) { // 그 외 예상치 못한 모든 예외
            log.error("환율 API 호출 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("환율 API 호출 중 알 수 없는 오류 발생", e);
        }
    }

    /**
     * 모든 환율 정보 조회 (필요 시 사용)
     */
    public ExchangeRateApiResponse fetchAllRates(String baseCurrency) {
        try {
            String url = String.format("%s%s", exchangeRateApiUrl, baseCurrency);
            String jsonResponse = httpClientUtil.get(url);

            return gson.fromJson(jsonResponse, ExchangeRateApiResponse.class);

        } catch (IOException e) {
            log.error("모든 환율 API HTTP 통신 실패: {}", e.getMessage(), e);
            throw new RuntimeException("모든 환율 API HTTP 통신 실패", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("모든 환율 API 호출 중 스레드 인터럽트 발생: {}", e.getMessage(), e);
            throw new RuntimeException("모든 환율 API 호출 중 스레드 인터럽트 발생", e);
        } catch (JsonSyntaxException e) {
            log.error("모든 환율 API 응답 JSON 파싱 실패: {}", e.getMessage(), e);
            throw new RuntimeException("모든 환율 API 응답 JSON 파싱 실패", e);
        } catch (Exception e) {
            log.error("모든 환율 API 호출 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("모든 환율 API 호출 중 알 수 없는 오류 발생", e);
        }
    }
}