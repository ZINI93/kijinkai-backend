package com.kijinkai.domain.payment.application.dto;

import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class PaymentDepositRequestDto {

    PaymentMethod paymentMethod;
    BigDecimal amountOriginal;
    BankType bankType;
    String depositor;
}
