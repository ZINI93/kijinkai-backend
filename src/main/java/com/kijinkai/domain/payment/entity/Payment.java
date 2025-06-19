package com.kijinkai.domain.payment.entity;

import com.kijinkai.domain.BaseEntity;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.entity.Currency;
import com.kijinkai.domain.wallet.entity.Wallet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
@Entity
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, updatable = false, unique = true)
    private Long paymentId;

    @Column(name = "payment_uuid", nullable = false, updatable = false, unique = true)
    private String paymentUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;

    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private BigDecimal amountOriginal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currencyOriginal;

    @Column(nullable = false)
    private BigDecimal  amountConverter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currencyConverter;

    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    private String description;

    @Column(name = "external_transaction_id", updatable = false)
    private String externalTransactionId;

    @Column(name = "exchange_rate", updatable = false, nullable = false)
    private BigDecimal exchangeRate;


    @Builder
    public Payment(String paymentUuid, Customer customer, Wallet wallet, Order order, PaymentStatus paymentStatus, PaymentMethod paymentMethod, BigDecimal amountOriginal, Currency currencyOriginal, BigDecimal amountConverter, Currency currencyConverter, PaymentType paymentType, String description, String externalTransactionId, BigDecimal exchangeRate) {
        this.paymentUuid = paymentUuid != null ? paymentUuid : UUID.randomUUID().toString();
        this.customer = customer;
        this.wallet = wallet;
        this.order = order;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
        this.paymentMethod = paymentMethod;
        this.amountOriginal = amountOriginal;
        this.currencyOriginal = currencyOriginal !=null ? currencyOriginal : Currency.JPY;
        this.amountConverter = amountConverter;
        this.currencyConverter = currencyConverter;
        this.paymentType = paymentType;
        this.description = description;
        this.externalTransactionId = externalTransactionId;
        this.exchangeRate = exchangeRate;
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
