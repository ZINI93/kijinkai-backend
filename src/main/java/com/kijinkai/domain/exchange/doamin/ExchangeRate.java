package com.kijinkai.domain.exchange.doamin;

import com.kijinkai.domain.common.TimeBaseEntity;
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
    @Column(name = "exchange_rate_id")
    private Long exchangeRateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private BigDecimal rate;

    @Column(name = "fetch_at", nullable = false)
    private LocalDateTime fetchedAt;

    @Builder
    public ExchangeRate(Currency currency, Currency toCurrency, BigDecimal rate, LocalDateTime fetchedAt) {
        this.currency = currency;
        this.rate = rate;
        this.fetchedAt = fetchedAt;
    }

    public void updateExchangeRate(BigDecimal rate) {
        this.rate = rate;
    }

}
