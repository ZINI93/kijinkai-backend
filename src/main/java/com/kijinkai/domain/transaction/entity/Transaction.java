package com.kijinkai.domain.transaction.entity;


import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.payment.domain.exception.PaymentProcessingException;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "transactions")
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

    @Comment("각각의 거래 코드")
    @Column(name = "payment_code")
    private String paymentCode;

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



    public void completedPayment(){
        if (this.transactionStatus != TransactionStatus.REQUEST){
            throw new PaymentProcessingException("요청 상태에서만 처리완료를 해줄수 있습니다.");
        }

        this.transactionStatus = TransactionStatus.COMPLETED;
    }


    public void failedPayment(){
        if (this.transactionStatus != TransactionStatus.REQUEST){
            throw new PaymentProcessingException("요청 상태에서만 가능합니다.");
        }

        this.transactionStatus = TransactionStatus.FAILED;
    }
}
