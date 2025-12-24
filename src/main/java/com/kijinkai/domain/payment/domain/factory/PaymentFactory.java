package com.kijinkai.domain.payment.domain.factory;


import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.DepositRequestJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.OrderPaymentJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.RefundRequestJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.WithdrawRequestJpaEntity;
import com.kijinkai.domain.payment.domain.enums.*;
import com.kijinkai.domain.payment.domain.model.DepositRequest;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import com.kijinkai.domain.payment.domain.model.RefundRequest;
import com.kijinkai.domain.payment.domain.model.WithdrawRequest;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
                .requestUuid(UUID.randomUUID())
                .customerUuid(customer.getCustomerUuid())
                .status(WithdrawStatus.PENDING_ADMIN_APPROVAL)
                .walletUuid(wallet.getWalletUuid())
                .requestAmount(requestAmount)
                .withdrawFee(withdrawFee)
                .targetCurrency(tagetCurrency)
                .exchangeRate(exchangeRate)
                .totalDeductAmount(requestAmount.add(withdrawFee))
                .convertedAmount(convertedAmount)
                .accountNumber(accountNumber)
                .bankName(bankName)
                .accountHolder(accountHolder)
                .expiresAt(LocalDateTime.now().plusDays(3))
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
            Customer customer, Wallet wallet, UUID adminUuid) {
        return OrderPayment.builder()
                .paymentUuid(UUID.randomUUID())
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .paymentAmount(BigDecimal.ZERO)
                .paymentType(PaymentType.PRODUCT_PAYMENT)
                .orderPaymentStatus(OrderPaymentStatus.COMPLETED)
                .paymentOrder(PaymentOrder.FIRST)
                .createdByAdminUuid(adminUuid)
                .build();
    }
    public OrderPayment createOrderSecondPayment(
            Customer customer, BigDecimal paymentAmount, com.kijinkai.domain.wallet.domain.model.Wallet wallet, UUID adminUuid) {
        return OrderPayment.builder()
                .paymentUuid(UUID.randomUUID())
                .customerUuid(customer.getCustomerUuid())
                .paymentType(PaymentType.SHIPPING_PAYMENT)
                .paymentOrder(PaymentOrder.SECOND)
                .walletUuid(wallet.getWalletUuid())
                .orderPaymentStatus(OrderPaymentStatus.PENDING)
                .paymentAmount(paymentAmount)
                .createdByAdminUuid(adminUuid)
                .build();
    }

}
