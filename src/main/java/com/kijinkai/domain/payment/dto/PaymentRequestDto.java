package com.kijinkai.domain.payment.dto;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.orderitem.entity.Currency;
import com.kijinkai.domain.payment.entity.PaymentMethod;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import com.kijinkai.domain.payment.entity.PaymentType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class PaymentRequestDto {

    private PaymentMethod paymentMethod;
    private BigDecimal amountOriginal;
    private Currency currencyConverter;

    @Builder
    public PaymentRequestDto(PaymentMethod paymentMethod, BigDecimal amountOriginal, Currency currencyConverter) {
        this.paymentMethod = paymentMethod;
        this.amountOriginal = amountOriginal;
        this.currencyConverter = currencyConverter;
    }
}
