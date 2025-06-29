package com.kijinkai.domain.exchange.repository;

import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.exchange.doamin.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    /**
     * 특정 통화 쌍의 최신 환율 조회
     */
    Optional<ExchangeRate> findByFromCurrencyAndToCurrency(Currency fromCurrency, Currency toCurrency);

    /**
     * 기준 통화의 모든 환율 조회 (업데이트 시간 내림차순)
     */
    List<ExchangeRate> findByFromCurrencyOrderByUpdatedAtDesc(Currency fromCurrency);

    /**
     * 최근 업데이트된 환율들 조회
     */
    @Query("SELECT e FROM ExchangeRate e WHERE e.updatedAt >= :since ORDER BY e.updatedAt DESC")
    List<ExchangeRate> findRecentlyUpdated(@Param("since") LocalDateTime since);

    /**
     * 특정 환율 쌍이 존재하는지 확인
     */
    boolean existsByFromCurrencyAndToCurrency(Currency fromCurrency, Currency toCurrency);

}