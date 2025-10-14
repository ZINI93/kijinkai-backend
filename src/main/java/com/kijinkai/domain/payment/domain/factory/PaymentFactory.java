package com.kijinkai.domain.payment.domain.factory;


import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.domain.entity.*;
import com.kijinkai.domain.payment.domain.enums.*;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentFactory {

    public DepositRequest createDepositRequest(
            Customer customer, Wallet wallet, BigDecimal originalAmount, Currency originalCurrency,
            BigDecimal convertAmount, BigDecimal exchangeRate, String depositorName, BankType bankType
    ) {

        return DepositRequest.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .amountOriginal(originalAmount)
                .currencyOriginal(originalCurrency)
                .amountConverted(convertAmount)
                .exchangeRate(exchangeRate)
                .depositorName(depositorName)
                .bankType(bankType)
                .build();
    }

    public WithdrawRequest createWithdrawRequest(
            Customer customer, Wallet wallet, BigDecimal requestAmount, Currency tagetCurrency
            , BigDecimal withdrawFee, String bankName, String accountHolder, BigDecimal convertedAmount, String accountNumber,
            BigDecimal exchangeRate) {

        return WithdrawRequest.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .requestAmount(requestAmount)
                .withdrawFee(withdrawFee)
                .targetCurrency(tagetCurrency)
                .exchangeRate(exchangeRate)
                .convertedAmount(convertedAmount)
                .accountNumber(accountNumber)
                .bankName(bankName)
                .accountHolder(accountHolder)
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
            Customer customer, Wallet wallet) {
        return OrderPayment.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .paymentAmount(BigDecimal.ZERO)
                .paymentType(PaymentType.PRODUCT_PAYMENT)
                .orderPaymentStatus(OrderPaymentStatus.COMPLETED)
                .paymentOrder(PaymentOrder.FIRST)
                .build();
    }
    public OrderPayment createOrderSecondPayment(
            Customer customer, BigDecimal paymentAmount, com.kijinkai.domain.wallet.domain.model.Wallet wallet, UUID adminUuid) {
        return OrderPayment.builder()
                .customerUuid(customer.getCustomerUuid())
                .paymentType(PaymentType.SHIPPING_PAYMENT)
                .walletUuid(wallet.getWalletUuid())
                .orderPaymentStatus(OrderPaymentStatus.PENDING)
                .paymentAmount(paymentAmount)
                .createdByAdminUuid(adminUuid)
                .build();
    }

}
