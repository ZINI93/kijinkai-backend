package com.kijinkai.domain.wallet.application.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class WalletBalanceResponseDto {

    BigDecimal balance;


}

