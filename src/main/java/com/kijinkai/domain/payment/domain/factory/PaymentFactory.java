package com.kijinkai.domain.payment.domain.factory;


import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.DepositRequestJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.OrderPaymentJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.RefundRequestJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.WithdrawRequestJpaEntity;
import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
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

//    public DepositRequest createDepositRequest(
//            Customer customer, Wallet wallet, BigDecimal originalAmount, Currency originalCurrency,
//            BigDecimal convertAmount, BigDecimal exchangeRate, String depositorName, BankType bankType
//    ) {
//
//        return DepositRequest.builder()
//                .customerUuid(customer.getCustomerUuid())
//                .walletUuid(wallet.getWalletUuid())
//                .amountOriginal(originalAmount)
//                .currencyOriginal(originalCurrency)
//                .depositorName(depositorName)
//                .bankType(bankType)
//                .build();
//    }

    public WithdrawRequest createWithdrawRequest(
            Customer customer, Wallet wallet, BigDecimal requestAmount, Currency tagetCurrency
            , BigDecimal withdrawFee, BankType bankType, String accountHolder, String accountNumber, String withdrawCode) {

        return WithdrawRequest.builder()
                .requestUuid(UUID.randomUUID())
                .withdrawCode(withdrawCode)
                .customerUuid(customer.getCustomerUuid())
                .status(WithdrawStatus.PENDING_ADMIN_APPROVAL)
                .walletUuid(wallet.getWalletUuid())
                .requestAmount(requestAmount)
                .withdrawFee(withdrawFee)
                .targetCurrency(tagetCurrency)
                .totalDeductAmount(requestAmount.add(withdrawFee))
                .accountNumber(accountNumber)
                .bankType(bankType)
                .accountHolder(accountHolder)
                .build();
    }


    public RefundRequest createRefundPayment(UUID userAdminUuid, BigDecimal refundAmount, String refundCode, Customer customer, Wallet wallet, OrderItem orderItem, RefundRequestDto requestDto) {

        return RefundRequest.builder()
                .refundUuid(UUID.randomUUID())
                .refundCode(refundCode)
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .orderItemUuid(orderItem.getOrderItemUuid())
                .refundAmount(refundAmount)
                .refundReason(requestDto.getRefundReason())
                .refundType(requestDto.getRefundType())
                .adminMemo(requestDto.getAdminMemo())
                .processedByAdmin(userAdminUuid)
                .status(RefundStatus.COMPLETED)
                .build();
    }

    public OrderPayment createProductPayment(
            UUID customerUuid, UUID walletUuid, String paymentCode, BigDecimal totalAmount,
            BigDecimal discountAmount) {
        return OrderPayment.builder()
                .paymentUuid(UUID.randomUUID())
                .customerUuid(customerUuid)
                .orderPaymentCode(paymentCode)
                .walletUuid(walletUuid)
                .paymentAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalPaymentAmount(totalAmount.subtract(discountAmount))
                .paymentType(PaymentType.PRODUCT_PAYMENT)
                .orderPaymentStatus(OrderPaymentStatus.COMPLETED)
                .paymentOrder(PaymentOrder.FIRST)
                .build();
    }

    /*
    삭제예정
     */
    public OrderPayment createOrderSecondPayment(
            UUID customerUuid, BigDecimal paymentAmount, String orderPaymentCode, UUID walletUuid) {
        return OrderPayment.builder()
                .paymentUuid(UUID.randomUUID())
                .walletUuid(walletUuid)
                .orderPaymentCode(orderPaymentCode)
                .customerUuid(customerUuid)
                .paymentType(PaymentType.SHIPPING_PAYMENT)
                .paymentOrder(PaymentOrder.SECOND)
                .orderPaymentStatus(OrderPaymentStatus.COMPLETED)
                .paymentAmount(paymentAmount)
                .build();
    }


    public OrderPayment createDeliveryPayment(String orderPaymentCode, BigDecimal amount, UUID deliveryUuid,UUID customerUuid, UUID walletUuid){

        return OrderPayment.builder()
                .paymentUuid(UUID.randomUUID())
                .customerUuid(customerUuid)
                .walletUuid(walletUuid)
                .orderPaymentCode(orderPaymentCode)
                .deliveryUuid(deliveryUuid)
                .paymentType(PaymentType.SHIPPING_PAYMENT)
                .paymentOrder(PaymentOrder.SECOND)
                .orderPaymentStatus(OrderPaymentStatus.PENDING)
                .discountAmount(BigDecimal.ZERO)
                .paymentAmount(amount)
                .finalPaymentAmount(amount) // 배송비는 쿠폰적용 불가 이므로 최종가격이 같음
                .build();
    }

}
