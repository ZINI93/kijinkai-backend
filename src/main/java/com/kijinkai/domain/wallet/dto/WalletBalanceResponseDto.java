package com.kijinkai.domain.wallet.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class WalletBalanceResponseDto {

    BigDecimal balance;
}

