//package com.kijinkai.domain.exchange.service;
//
//import com.kijinkai.domain.exchange.client.ExchangeRateApiClient;
//import com.kijinkai.domain.exchange.doamin.ExchangeRate;
//import com.kijinkai.domain.exchange.dto.ExchangeApiResponse;
//import com.kijinkai.domain.exchange.repository.ExchangeRateRepository;
//import com.kijinkai.domain.orderitem.entity.Currency;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class ExchangeRateServiceImpl implements ExchangeRateService {
//
//    private final ExchangeRateApiClient exchangeRateApiClient;
//    private final ExchangeRateRepository exchangeRateRepository;
//
//    @Override
//    public BigDecimal getExchangeRate(Currency from, Currency to) {
//        // 1. 캐시 or DB 확인
//        Optional<ExchangeRate> optionalRate = exchangeRateRepository
//                .findTopByFromCurrencyAndToCurrencyOrderByFetchedAtDesc(from, to);
//
//        if (optionalRate.isPresent() && optionalRate.get().getFetchedAt().isAfter(LocalDateTime.now().minusHours(1))) {
//            return optionalRate.get().getRate();
//        }
//
//        // 2. API 호출
//        ExchangeApiResponse response = exchangeRateApiClient.getExchangeRates(from);
//        BigDecimal rate = response.getRates().get(to);
//
//        // 3. 저장
//        ExchangeRate saved = ExchangeRate.builder()
//                .fromCurrency(from)
//                .toCurrency(to)
//                .rate(rate)
//                .fetchedAt(LocalDateTime.now())
//                .build();
//
//        exchangeRateRepository.save(saved);
//        return rate;
//    }
//}
//
