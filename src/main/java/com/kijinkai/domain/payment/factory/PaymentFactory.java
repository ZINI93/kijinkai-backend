package com.kijinkai.domain.payment.factory;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.payment.dto.PaymentDepositRequestDto;
import com.kijinkai.domain.payment.dto.WithdrawalRequestDto;
import com.kijinkai.domain.payment.entity.Payment;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import com.kijinkai.domain.payment.entity.PaymentType;
import com.kijinkai.domain.wallet.entity.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentFactory {

    public Payment createWithDepositPayment(Customer customer, Wallet wallet, BigDecimal amountOriginal, BigDecimal amountConverter, PaymentDepositRequestDto requestDto){

        return Payment.builder()
                .paymentUuid(UUID.randomUUID())
                .customer(customer)
                .wallet(wallet)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(requestDto.getPaymentMethod())
                .currencyOriginal(Currency.KRW)
                .currencyConverter(Currency.JPY)
                .amountOriginal(amountOriginal)
                .amountConverter(amountConverter)
                .paymentType(PaymentType.DEPOSIT)
                .description(null)
                .externalTransactionId(null)
                .build();
    }

    public Payment createWithWithdrawalPayment(Customer customer, Wallet wallet, BigDecimal amountOriginal, BigDecimal amountConverter, WithdrawalRequestDto requestDto){

        return Payment.builder()
                .paymentUuid(UUID.randomUUID())
                .customer(customer)
                .wallet(wallet)
                .paymentStatus(PaymentStatus.PENDING)
                .currencyOriginal(Currency.JPY)
                .currencyConverter(Currency.KRW)
                .amountOriginal(amountOriginal)
                .amountConverter(amountConverter)
                .paymentType(PaymentType.WITHDRAWAL)
                .bankName(requestDto.getBankName())
                .amountNumber(requestDto.getAmountNumber())
                .amountHolder(requestDto.getAmountHolder())
                .description(null)
                .externalTransactionId(null)
                .build();
    }



    public Payment createWithRefundPayment(OrderItem orderItem, UUID adminUuid, Wallet wallet, BigDecimal amountOriginal, String reason){

        return Payment.builder()
                .paymentUuid(UUID.randomUUID())
                .customer(orderItem.getOrder().getCustomer())
                .wallet(wallet)
                .paymentStatus(PaymentStatus.PENDING)
                .currencyOriginal(Currency.JPY)
                .currencyConverter(null)
                .amountOriginal(amountOriginal)
                .amountConverter(null)
                .paymentType(PaymentType.REFUND)
                .refundReason(reason)
                .refundAdminUuid(adminUuid)
                .build();
    }




}
