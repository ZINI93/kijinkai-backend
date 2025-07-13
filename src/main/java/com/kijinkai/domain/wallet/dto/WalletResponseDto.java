package com.kijinkai.domain.wallet.dto;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.wallet.entity.WalletStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
public class WalletResponseDto {

    private UUID walletUuid;
    private Customer customer;
    private BigDecimal balance;
    private Currency currency;
    private WalletStatus walletStatus;
    private Long version;

    @Builder
    public WalletResponseDto(UUID walletUuid, Customer customer, BigDecimal balance, Currency currency, WalletStatus walletStatus, Long version) {
        this.walletUuid = walletUuid;
        this.customer = customer;
        this.balance = balance;
        this.currency = currency;
        this.walletStatus = walletStatus;
        this.version = version;
    }
}
