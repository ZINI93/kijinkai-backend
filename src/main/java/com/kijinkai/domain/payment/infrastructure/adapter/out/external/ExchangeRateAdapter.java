package com.kijinkai.domain.payment.infrastructure.adapter.out.external;


import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.payment.application.port.out.ExchangePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class ExchangeRateAdapter implements ExchangePort {

    private ExchangeRateService exchangeRateService;

    @Override
    public BigDecimal exchangeRate(Currency originalCurrency, Currency targetCurrency) {
        return exchangeRateService.getExchangeRate(originalCurrency, targetCurrency);
    }
}
