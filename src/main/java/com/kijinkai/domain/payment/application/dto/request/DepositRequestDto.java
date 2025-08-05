package com.kijinkai.domain.payment.application.dto.request;

import com.kijinkai.domain.delivery.entity.Carrier;
import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class DepositRequestDto {

    BigDecimal amountOriginal;
    Currency OriginalCurrency;
    String depositorName;
    String bankAccount;
    String memo;
}
