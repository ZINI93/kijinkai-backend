package com.kijinkai.domain.transaction.entity;


import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.domain.model.Wallet;
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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
//    public CustomerJpaEntity customerJpaEntity;


    @Column(name = "customer_uuid")
    private UUID customerUuid;

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
    public Transaction(UUID transactionUuid, UUID customerUuid, Wallet wallet, Order order, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, Currency currency , TransactionStatus transactionStatus, String memo) {
        this.transactionUuid = transactionUuid != null ? transactionUuid : UUID.randomUUID();
        this.customerUuid = customerUuid;
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
