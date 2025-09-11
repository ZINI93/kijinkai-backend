package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.dto.ExchangeRateRequestDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateUpdateDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ExchangePort {

    ExchangeRateResponseDto createExchangeRate(UUID adminUuid, ExchangeRateRequestDto requestDto);
    ExchangeRateResponseDto updateExchangeRate(UUID adminUuid, Long exchangeId, ExchangeRateUpdateDto updateDto);
    ExchangeRateResponseDto getExchangeRateInfo(Long exchangeId);
    ExchangeRateResponseDto getExchangeRateInfoByCurrency(Currency currency);
    List<ExchangeRateResponseDto> getExchangeRates();
    void removeExchangeRate(UUID adminUuid, Long exchangeId);
}
