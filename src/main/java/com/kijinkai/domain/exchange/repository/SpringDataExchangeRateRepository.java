package com.kijinkai.domain.exchange.repository;

import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// JpaRepository를 상속받아 기본적인 CRUD를 제공받습니다.
public interface SpringDataExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    // 가장 최신 환율을 조회하기 위한 쿼리
    @Query("SELECT er FROM ExchangeRate er WHERE er.fromCurrency = :fromCurrency AND er.toCurrency = :toCurrency ORDER BY er.fetchedAt DESC LIMIT 1")
    Optional<ExchangeRate> findLatestByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
}

// 실제 도메인 리포지토리 인터페이스를 구현하는 클래스
@Repository // Spring Bean으로 등록
class ExchangeRateRepositoryImpl implements ExchangeRateRepository {

    private final SpringDataExchangeRateRepository springDataRepository;

    public ExchangeRateRepositoryImpl(SpringDataExchangeRateRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        return springDataRepository.save(exchangeRate);
    }

    @Override
    public Optional<ExchangeRate> findLatestExchangeRate(String fromCurrency, String toCurrency) {
        return springDataRepository.findLatestByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
    }
}