package com.kijinkai.domain.exchange.mapper;


import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateMapper {


    public ExchangeRateResponseDto toResponse(ExchangeRate exchangeRate){
        return ExchangeRateResponseDto.builder()
                .rate(exchangeRate.getRate())
                .build();
    }
}
