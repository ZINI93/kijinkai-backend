package com.kijinkai.domain.exchange.doamin;

import com.kijinkai.domain.TimeBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "exchange_rates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeRate extends TimeBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromCurrency;
    private String toCurrency;
    private BigDecimal rate;
    private LocalDateTime fetchedAt;

    @Builder
    public ExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate, LocalDateTime fetchedAt) {
        // 도메인 규칙: 통화 코드는 3글자여야 하고, 환율은 양수여야 합니다.
        if (fromCurrency == null || fromCurrency.length() != 3) {
            throw new IllegalArgumentException("From currency must be a 3-letter code.");
        }
        if (toCurrency == null || toCurrency.length() != 3) {
            throw new IllegalArgumentException("To currency must be a 3-letter code.");
        }
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be a positive value.");
        }
        if (fetchedAt == null) {
            throw new IllegalArgumentException("Fetched date cannot be null.");
        }

        this.fromCurrency = fromCurrency.toUpperCase(); // 대문자로 저장
        this.toCurrency = toCurrency.toUpperCase();     // 대문자로 저장
        this.rate = rate;
        this.fetchedAt = fetchedAt;
    }

    // 환율 비교 로직 (만약 필요하다면 도메인 로직을 엔티티 안에 넣을 수 있음)
    public boolean isSignificantlyDifferent(BigDecimal newRate, BigDecimal threshold) {
        if (newRate == null) return false;
        BigDecimal diff = this.rate.subtract(newRate).abs();
        return diff.compareTo(threshold) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
