package com.kijinkai.domain.transaction.entity;


import com.kijinkai.domain.BaseEntity;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.entity.Currency;
import com.kijinkai.domain.wallet.entity.Wallet;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Table(name = "transactions")
@Entity
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "transaction_uuid", nullable = false)
    public UUID transactionUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    public Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    public Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false ,updatable = false)
    public Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    public TransactionType transactionType;

    @Column(nullable = false, updatable = false)
    public BigDecimal amount;

    @Column(name = "balance_before", nullable = false, updatable = false)
    public BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, updatable = false)
    public BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    public Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    public TransactionStatus transactionStatus;

    @Column(columnDefinition = "TEXT")
    public String memo;


    @Builder
    public Transaction(UUID transactionUuid, Customer customer, Wallet wallet, Order order, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, Currency currency , TransactionStatus transactionStatus, String memo) {
        this.transactionUuid = transactionUuid != null ? transactionUuid : UUID.randomUUID();
        this.customer = customer;
        this.wallet = wallet;
        this.order = order;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.currency = currency != null ? currency : Currency.JPY;
        this.transactionStatus = transactionStatus;
        this.memo = memo;
    }

}
