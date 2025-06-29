package com.kijinkai.domain.exchange.service;

import com.kijinkai.domain.exchange.client.ExchangeRateApiClient;
import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponse;
import com.kijinkai.domain.exchange.exception.CurrencyNotFoundException;
import com.kijinkai.domain.exchange.repository.ExchangeRateRepository;
import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor // final 필드를 주입받기 위한 Lombok 어노테이션
@Slf4j // Logger 자동 생성
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository; // final로 변경하여 @RequiredArgsConstructor로 주입
    private final ExchangeRateApiClient apiClient; // final로 변경하여 @RequiredArgsConstructor로 주입

    /**
     * 환율 조회 (다른 서비스에서 사용하는 메인 메서드)
     * @param fromCurrency 기준 통화
     * @param toCurrency 대상 통화
     * @return 환율 (BigDecimal)
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public BigDecimal getExchangeRate(Currency fromCurrency, Currency toCurrency) {
        // 통화 유효성 검증은 Enum 자체로 대체 가능 (Enum.valueOf 등으로)
        // 하지만 Currency Enum에 isValidCode 메서드가 있으니 활용
        validateCurrency(fromCurrency);
        validateCurrency(toCurrency);

        // 같은 통화인 경우
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        ExchangeRate exchangeRate = exchangeRateRepository
                .findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
                .orElseThrow(() -> new CurrencyNotFoundException(
                        String.format("환율을 찾을 수 없습니다: %s → %s", fromCurrency.name(), toCurrency.name())));

        return exchangeRate.getRate();
    }

    /**
     * 금액 환전 계산 (다른 서비스에서 사용)
     * @param amount 금액
     * @param fromCurrency 기준 통화
     * @param toCurrency 대상 통화
     * @return 환전된 금액
     */
    public BigDecimal convertAmount(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("금액은 0 이상이어야 합니다.");
        }

        BigDecimal rate = getExchangeRate(fromCurrency, toCurrency);
        // 환전 결과는 보통 2자리까지 표시 (예: 123.45)
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * JPY 기준 주요 통화 환율 조회 (구매대행에서 주로 사용)
     */
    @Transactional(readOnly = true)
    public List<ExchangeRateResponse> getJpyBasedRates() {
        List<ExchangeRate> rates = exchangeRateRepository
                .findByFromCurrencyOrderByUpdatedAtDesc(Currency.JPY);

        return rates.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 환율 업데이트 (스케줄러에서 사용)
     */
    public void updateExchangeRates() {
        log.info("환율 업데이트 시작"); // 로거 사용
        try {
            // JPY 기준으로 주요 통화 환율 업데이트
            updateJpyBasedRates();
            log.info("환율 업데이트 완료: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("환율 업데이트 실패: {}", e.getMessage(), e); // 에러 로깅 시 스택 트레이스 포함
            throw e; // 호출자에게 예외 전파
        }
    }

    /**
     * 특정 통화 쌍 환율 업데이트
     */
    public void updateSpecificRate(Currency fromCurrency, Currency toCurrency) {
        try {
            // 외부 API에서 환율 조회 (여기서는 Currency Enum을 String으로 변환하여 전달)
            BigDecimal rate = apiClient.fetchExchangeRate(fromCurrency.name(), toCurrency.name());
            LocalDateTime fetchedAt = LocalDateTime.now(); // 환율을 가져온 시간

            // DB에서 기존 환율 정보 조회 또는 새로 생성
            ExchangeRate exchangeRate = exchangeRateRepository
                    .findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
                    .orElseGet(() -> new ExchangeRate(fromCurrency, toCurrency, rate, fetchedAt)); // 없으면 새로 생성

            // 환율 및 가져온 시간 업데이트
            exchangeRate.setRate(rate);
            exchangeRate.setFetchedAt(fetchedAt);

            // DB에 저장 (JPA Auditing이 createdAt, updatedAt 자동 관리)
            exchangeRateRepository.save(exchangeRate);
            log.info("환율 업데이트 성공: {} -> {}, Rate: {}", fromCurrency.name(), toCurrency.name(), rate);

        } catch (Exception e) {
            log.error("환율 업데이트 실패: {} → {} | 에러: {}", fromCurrency.name(), toCurrency.name(), e.getMessage(), e);
            throw new RuntimeException("환율 업데이트 실패: " + fromCurrency.name() + " → " + toCurrency.name(), e);
        }
    }

    /**
     * 환율 정보 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public ExchangeRateResponse getExchangeRateInfo(Currency fromCurrency, Currency toCurrency) {
        ExchangeRate exchangeRate = exchangeRateRepository
                .findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
                .orElseThrow(() -> new CurrencyNotFoundException(
                        String.format("환율 정보를 찾을 수 없습니다: %s → %s", fromCurrency.name(), toCurrency.name())));

        return convertToResponse(exchangeRate);
    }

    /**
     * 최근 업데이트된 환율 조회
     */
    @Transactional(readOnly = true)
    public List<ExchangeRateResponse> getRecentlyUpdatedRates(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<ExchangeRate> rates = exchangeRateRepository.findRecentlyUpdated(since);

        return rates.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Private 메서드들
    private void updateJpyBasedRates() {
        // JPY 기준으로 KRW, PHP, USD 환율을 업데이트
        Arrays.asList(Currency.KRW, Currency.JPY, Currency.CLP).forEach(targetCurrency ->
                updateSpecificRate(Currency.JPY, targetCurrency)
        );
    }

    private void validateCurrency(Currency currency) {
        // Currency Enum 자체가 유효성을 보장하지만, 혹시 null이 들어올 경우를 대비
        if (currency == null) {
            throw new IllegalArgumentException("통화 코드가 null입니다.");
        }
        // Currency Enum의 fromCode 메서드에서 이미 유효성 검사를 하므로 별도 isValidCode 체크는 생략 가능
        // 만약 fromCode를 사용하지 않고 직접 생성한다면, Currency.isValidCode(currency.name()) 호출
    }

    private ExchangeRateResponse convertToResponse(ExchangeRate exchangeRate) {
        return new ExchangeRateResponse(
                exchangeRate.getFromCurrency(),
                exchangeRate.getToCurrency(),
                exchangeRate.getRate(),
                exchangeRate.getFetchedAt() // 변경된 필드 이름 사용
        );
    }
}