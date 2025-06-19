package com.kijinkai.domain.wallet.dto;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.orderitem.entity.Currency;
import com.kijinkai.domain.wallet.entity.WalletStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class WalletResponseDto {

    private String walletUuid;
    private Customer customer;
    private BigDecimal balance;
    private Currency currency;
    private WalletStatus walletStatus;
    private Long version;

    @Builder
    public WalletResponseDto(String walletUuid, Customer customer, BigDecimal balance, Currency currency, WalletStatus walletStatus, Long version) {
        this.walletUuid = walletUuid;
        this.customer = customer;
        this.balance = balance;
        this.currency = currency;
        this.walletStatus = walletStatus;
        this.version = version;
    }
}
