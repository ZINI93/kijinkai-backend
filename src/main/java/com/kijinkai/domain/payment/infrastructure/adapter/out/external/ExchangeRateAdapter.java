package com.kijinkai.domain.payment.infrastructure.adapter.out.external;


import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.dto.ExchangeRateRequestDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.dto.ExchangeRateUpdateDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.payment.application.port.out.ExchangePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ExchangeRateAdapter implements ExchangePort {

    private final ExchangeRateService exchangeRateService;


    @Override
    public ExchangeRateResponseDto createExchangeRate(UUID adminUuid, ExchangeRateRequestDto requestDto) {
        return exchangeRateService.createExchangeRate(adminUuid, requestDto);
    }

    @Override
    public ExchangeRateResponseDto updateExchangeRate(UUID adminUuid, Long exchangeId, ExchangeRateUpdateDto updateDto) {
        return exchangeRateService.updateExchangeRate(adminUuid, exchangeId, updateDto);
    }

    @Override
    public ExchangeRateResponseDto getExchangeRateInfo(Long exchangeId) {
        return exchangeRateService.getExchangeRateInfo(exchangeId);
    }

    @Override
    public ExchangeRateResponseDto getExchangeRateInfoByCurrency(Currency Currency) {
        return exchangeRateService.getExchangeRateInfoByCurrency(Currency);
    }

    @Override
    public List<ExchangeRateResponseDto> getExchangeRates() {
        return exchangeRateService.getExchangeRates();
    }

    @Override
    public void removeExchangeRate(UUID adminUuid, Long exchangeId) {
        exchangeRateService.deleteExchangeRate(adminUuid, exchangeId);
    }
}
