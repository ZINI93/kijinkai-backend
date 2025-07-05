package com.kijinkai.domain.wallet.entity;


import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Table(name = "wallets")
@Entity
public class Wallet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    @Column(name = "wallet_uuid", nullable = false, updatable = false, unique = true)
    private UUID walletUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false, unique = true)
    private Customer customer;

    @Column(nullable = false,  precision = 16 ,scale = 4)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(name = "wallet_status", nullable = false)
    private WalletStatus walletStatus;

    @Version
    @Column(nullable = false)
    private Long version;


    @Builder
    public Wallet(UUID walletUuid, Customer customer, BigDecimal balance, Currency currency, WalletStatus walletStatus, Long version) {
        this.walletUuid = walletUuid != null ? walletUuid : UUID.randomUUID();
        this.customer = customer;
        this.balance = balance;
        this.currency = currency != null ? currency : Currency.JPY;
        this.walletStatus = walletStatus;
        this.version = version;
    }


    public void updateWalletBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void updateWalletWalletStatus(WalletStatus walletStatus) {
        this.walletStatus = walletStatus;
    }

    /**
     * 월렛 잔액을 증가시키는 비즈니스 로직.
     * 잔액 변경은 Wallet 엔티티 내부에서 직접 처리합니다.
     *
     * @param amount 증가시킬 금액 (양수여야 함)
     */
    public void increaseBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount to increase must be a positive value");
        }
        // !!! 핵심: 새로운 BigDecimal 객체를 현재 balance 필드에 다시 할당 !!!
        this.balance = this.balance.add(amount);
    }

    /**
     * 월렛 잔액을 감소시키는 비즈니스 로직.
     *
     * @param amount 감소시킬 금액 (양수여야 함)
     */
    public void decreaseBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount to decrease must be a positive value");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance for withdrawal");
        }
        this.balance = this.balance.subtract(amount);
    }

}
