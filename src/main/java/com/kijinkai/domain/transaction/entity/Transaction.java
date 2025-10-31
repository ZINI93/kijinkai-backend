package com.kijinkai.domain.transaction.entity;


import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Table(name = "transactions")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "transaction_uuid", nullable = false, unique = true)
    private UUID transactionUuid;

    @Column(name = "customer_uuid", nullable = false)
    private UUID customerUuid;

    @Column(name = "wallet_uuid",nullable = false)
    private UUID walletUuid;

    @Column(name = "order_uuid", nullable = false)
    private UUID orderUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "balance_before", nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, updatable = false, length = 20)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false, length = 20)
    private TransactionStatus transactionStatus;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;


    @Builder
    public Transaction(UUID transactionUuid, UUID customerUuid, UUID walletUuid, UUID orderUuid, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, Currency currency, TransactionStatus transactionStatus, String memo) {
        this.transactionUuid = transactionUuid != null ? transactionUuid : UUID.randomUUID();
        this.customerUuid = customerUuid;
        this.walletUuid = walletUuid;
        this.orderUuid = orderUuid;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.currency = currency != null ? currency : Currency.JPY;
        this.transactionStatus = transactionStatus;
        this.memo = memo;
    }

}
