package com.kijinkai.domain.payment.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.entity.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class PaymentDepositRequestDto {

    private PaymentMethod paymentMethod;
    private BigDecimal amountOriginal;
    private String depositor;

    @Builder
    public PaymentDepositRequestDto(PaymentMethod paymentMethod, BigDecimal amountOriginal, String depositor) {
        this.paymentMethod = paymentMethod;
        this.amountOriginal = amountOriginal;
        this.depositor = depositor;
    }
}
