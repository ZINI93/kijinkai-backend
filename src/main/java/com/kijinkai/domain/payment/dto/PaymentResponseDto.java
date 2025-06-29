package com.kijinkai.domain.payment.dto;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.entity.PaymentMethod;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import com.kijinkai.domain.payment.entity.PaymentType;
import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Builder
@Setter
public class PaymentResponseDto {

    private UUID paymentUuid;
    private Customer customer;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private Currency currency;
    private PaymentType paymentType;
    private String description;
    private String externalTransactionId;
}
