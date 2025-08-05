package com.kijinkai.domain.payment.application.dto;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.PaymentMethod;
import com.kijinkai.domain.payment.domain.enums.PaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;


@Data
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


    //wallet
    private BigDecimal balance;

    public PaymentResponseDto() {
    }


    @Builder
    public PaymentResponseDto(UUID paymentUuid, Customer customer, BigDecimal balance, PaymentStatus paymentStatus, PaymentMethod paymentMethod, BigDecimal amount, Currency currency, PaymentType paymentType, String description, String externalTransactionId) {
        this.paymentUuid = paymentUuid;
        this.customer = customer;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.currency = currency;
        this.paymentType = paymentType;
        this.description = description;
        this.externalTransactionId = externalTransactionId;
        this.balance = balance;
    }
}
