package com.kijinkai.domain.exchange.doamin;

import com.kijinkai.domain.TimeBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromCurrency;
    private String toCurrency;
    private BigDecimal rate;
    private LocalDateTime fetchedAt;

    @Builder
    public ExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate, LocalDateTime fetchedAt) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
        this.fetchedAt = fetchedAt;
    }
}
