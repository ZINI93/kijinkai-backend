package com.kijinkai.domain.exchange.service;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.dto.ExchangeRateRequestDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateUpdateDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ExchangeRateService {

    ExchangeRateResponseDto createExchangeRate(UUID adminUuid, ExchangeRateRequestDto requestDto);

    ExchangeRateResponseDto updateExchangeRate(UUID adminUuid, Long exchangeId, ExchangeRateUpdateDto updateDto);

    ExchangeRateResponseDto getExchangeRateInfo(Long exchangeId);
    ExchangeRateResponseDto getExchangeRateInfoByCurrency(Currency currency);

    BigDecimal getExchangeRate(Long exchangeId);

    List<ExchangeRateResponseDto> getExchangeRates();


    void deleteExchangeRate(UUID adminUuid, Long exchangeId);

    //환율



}
