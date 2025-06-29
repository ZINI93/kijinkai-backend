package com.kijinkai.domain.payment.factory;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.dto.PaymentRequestDto;
import com.kijinkai.domain.payment.entity.Payment;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import com.kijinkai.domain.payment.entity.PaymentType;
import com.kijinkai.domain.wallet.entity.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentFactory {

    public Payment createPayment(Customer customer, Wallet wallet, BigDecimal amountOriginal, BigDecimal amountConverter,PaymentRequestDto requestDto){

        return Payment.builder()
                .paymentUuid(UUID.randomUUID())
                .customer(customer)
                .wallet(wallet)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(requestDto.getPaymentMethod())
                .currencyOriginal(Currency.KRW)
                .amountOriginal(amountOriginal)
                .amountConverter(amountConverter)
                .currencyConverter(requestDto.getCurrencyConverter())
                .paymentType(PaymentType.DEBIT)
                .description(null)
                .externalTransactionId(null)
                .build();
    }
}
