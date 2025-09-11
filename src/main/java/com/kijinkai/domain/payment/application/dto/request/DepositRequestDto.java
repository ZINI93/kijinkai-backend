package com.kijinkai.domain.payment.application.dto.request;

import com.kijinkai.domain.delivery.entity.Carrier;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.BankType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class DepositRequestDto {

    String depositorName;
    BigDecimal amountOriginal;
    BankType bankType;
    Currency originalCurrency;

    //승인 단계
    String memo;
}
