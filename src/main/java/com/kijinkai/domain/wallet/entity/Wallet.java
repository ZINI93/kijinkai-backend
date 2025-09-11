package com.kijinkai.domain.wallet.entity;


import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.wallet.dto.WalletFreezeRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Table(name = "wallets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "freeze_reason")
    private String freezeReason;

    @Version
    @Column(nullable = false)
    private Long version;


    @Builder
    public Wallet(UUID walletUuid, Customer customer, BigDecimal balance, Currency currency, WalletStatus walletStatus, String freezeReason, Long version) {
        this.walletUuid = walletUuid != null ? walletUuid : UUID.randomUUID();
        this.customer = customer;
        this.balance = balance;
        this.currency = currency != null ? currency : Currency.JPY;
        this.walletStatus = walletStatus;
        this.freezeReason = freezeReason;
        this.version = version;
    }


    public Wallet freeze(WalletFreezeRequest request){
        return Wallet.builder()
                .walletUuid(this.walletUuid)
                .customer(this.customer)
                .balance(this.balance)
                .walletStatus(WalletStatus.FROZEN)
                .freezeReason(request.getReason())
                .build();

    }

    public Wallet unfreeze(){

        return Wallet.builder()
                .walletUuid(this.walletUuid)
                .customer(this.customer)
                .balance(this.balance)
                .walletStatus(WalletStatus.ACTIVE)
                .build();

    }


    public boolean isActive(){
        return this.walletStatus == WalletStatus.ACTIVE;
    }

    public boolean isFrozen(){
        return this.walletStatus == WalletStatus.FROZEN;
    }
}
