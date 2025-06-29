package com.kijinkai.domain.exchange.doamin;

import com.kijinkai.domain.TimeBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.Objects;

@Entity
@Table(name = "exchange_rates",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_currency", "to_currency"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeRate extends TimeBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_currency", nullable = false)
    private Currency fromCurrency;

    @Column(name = "to_currency", nullable = false)
    private Currency toCurrency;

    @Column(nullable = false)
    private BigDecimal rate;

    @Column(name = "fetch_at", nullable = false)
    private LocalDateTime fetchedAt;

    @Builder
    public ExchangeRate(Currency fromCurrency, Currency toCurrency, BigDecimal rate, LocalDateTime fetchedAt) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be a positive value.");
        }
        if (fetchedAt == null) {
            throw new IllegalArgumentException("Fetched date cannot be null.");
        }

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
