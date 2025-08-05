package com.kijinkai.domain.payment.domain.factory;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.payment.domain.entity.*;
import com.kijinkai.domain.payment.application.dto.WithdrawalRequestDto;
import com.kijinkai.domain.payment.domain.enums.PaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import com.kijinkai.domain.payment.domain.enums.RefundType;
import com.kijinkai.domain.wallet.entity.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentFactory {

    public DepositRequest createDepositRequest(
            Customer customer, Wallet wallet, BigDecimal originalAmount, Currency originalCurrency,
            BigDecimal convertAmount, BigDecimal exchangeRate, String depositorName, String bankAccount
    ) {
        return DepositRequest.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .amountOriginal(originalAmount)
                .currencyOriginal(originalCurrency)
                .amountConverted(convertAmount)
                .exchangeRate(exchangeRate)
                .depositorName(depositorName)
                .bankAccount(depositorName)
                .build();
    }

    public WithdrawRequest createWithdrawRequest(
            Customer customer, Wallet wallet, BigDecimal requestAmount, Currency tagetCurrency
            , BigDecimal withdrawFee, String bankName, String accountHolder, BigDecimal convertedAmount) {

        return WithdrawRequest.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .requestAmount(requestAmount)
                .withdrawFee(withdrawFee)
                .targetCurrency(tagetCurrency)
                .convertedAmount(convertedAmount)
                .bankName(bankName)
                .accountHolder(accountHolder)
                .build();
    }


    public Payment createWithWithdrawalPayment(Customer customer, Wallet wallet, BigDecimal amountOriginal, BigDecimal amountConverter, WithdrawalRequestDto requestDto) {

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


    public Payment createWithRefundPayment(OrderItem orderItem, UUID adminUuid, Wallet wallet, BigDecimal amountOriginal, String reason) {

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

    public RefundRequest createRefundPayment(
            Customer customer, Wallet wallet, OrderItem orderItem
            , BigDecimal refundAmount, UUID adminUuid, String refundReason, RefundType refundType) {

        return RefundRequest.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .orderItemUuid(orderItem.getOrderItemUuid())
                .refundAmount(refundAmount)
                .refundReason(refundReason)
                .refundType(refundType)
                .processedByAdmin(adminUuid)
                .build();
    }

    public OrderPayment createOrderFirstPayment(
            Customer customer, Wallet wallet,
            Order order, BigDecimal paymentAmount, UUID adminUuid) {
        return OrderPayment.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .orderUuid(order.getOrderUuid())
                .paymentType(PaymentType.PRODUCT_PAYMENT)
                .paymentAmount(paymentAmount)
                .createdByAdminUuid(adminUuid)
                .build();
    }
    public OrderPayment createOrderSecondPayment(
            Customer customer, Wallet wallet,
            Order order, BigDecimal paymentAmount, UUID adminUuid) {
        return OrderPayment.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .orderUuid(order.getOrderUuid())
                .paymentType(PaymentType.SHIPPING_PAYMENT)
                .paymentAmount(paymentAmount)
                .createdByAdminUuid(adminUuid)
                .build();
    }

}
