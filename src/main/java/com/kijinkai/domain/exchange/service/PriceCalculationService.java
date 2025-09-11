package com.kijinkai.domain.exchange.service;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PriceCalculationService {

    private final ExchangeRateService exchangeRateService; // final로 변경하여 @RequiredArgsConstructor로 주입


    /**
     *  상품의 원가에 수수료 15%
     * @param jpyPrice
     * @return
     */
    public BigDecimal calculateTotalPrice(BigDecimal jpyPrice) {
        // 수수료 계산
        BigDecimal addFee = jpyPrice.multiply(BigDecimal.valueOf(1.15));

        return addFee;
    }
}

