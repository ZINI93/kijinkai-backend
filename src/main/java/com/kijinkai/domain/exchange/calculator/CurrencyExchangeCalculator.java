package com.kijinkai.domain.exchange.calculator;

import com.kijinkai.domain.exchange.exception.ExchangeRateApiException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CurrencyExchangeCalculator {

    public BigDecimal calculateConvertedAmount(BigDecimal originalAmount, BigDecimal exchangeRate) {

        return originalAmount.multiply(exchangeRate)
                .setScale(0, RoundingMode.HALF_UP);
    }

    public void validateConverterAmount(BigDecimal originalAmount, BigDecimal exchangeRate) {
        if (originalAmount.compareTo(BigDecimal.ZERO) > 0 || exchangeRate.compareTo(BigDecimal.ZERO) >= 0) {
            throw new ExchangeRateApiException(String.format("Invalid amount: originalAmount=%s, exchangeRate=%s. Both must be greater than zero.",
                    originalAmount, exchangeRate));
        }
    }

}
