package com.kijinkai.domain.wallet.dto;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.wallet.entity.WalletStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "사용자 지갑 고유 식별자", example = "xxxx-xxxx")
    private UUID walletUuid;

    @Schema(description = "고객 고유 식별자", example = "xxxx-xxxx")
    private Customer customer;

    @Schema(description = "보유 금액", example = "500000엔")
    private BigDecimal balance;

    @Schema(description = "통화", example = "JYP")
    private Currency currency;

    @Schema(description = "지갑 상태", example = "활성중")
    private WalletStatus walletStatus;

    @Schema(description = "version", example = "1")
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
