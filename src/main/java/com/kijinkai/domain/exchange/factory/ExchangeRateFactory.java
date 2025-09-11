package com.kijinkai.domain.exchange.factory;


import com.kijinkai.domain.delivery.entity.Delivery;
import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.exchange.dto.ExchangeRateRequestDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExchangeRateFactory {


    public ExchangeRate createExchangerate(ExchangeRateRequestDto requestDto){

        return ExchangeRate.builder()
                .currency(requestDto.getCurrency())
                .rate(requestDto.getRate())
                .fetchedAt(LocalDateTime.now())
                .build();
    }
}
