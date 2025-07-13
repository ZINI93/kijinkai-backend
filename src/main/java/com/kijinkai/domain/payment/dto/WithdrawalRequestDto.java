package com.kijinkai.domain.payment.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.entity.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequestDto {

    private PaymentMethod paymentMethod;
    private BigDecimal amountOriginal;
    private String bankName;
    private String amountNumber;
    private String amountHolder;


    @Builder
    public WithdrawalRequestDto(PaymentMethod paymentMethod, BigDecimal amountOriginal, String bankName, String amountNumber, String amountHolder) {
        this.paymentMethod = paymentMethod;
        this.amountOriginal = amountOriginal;
        this.bankName = bankName;
        this.amountNumber = amountNumber;
        this.amountHolder = amountHolder;
    }
}
