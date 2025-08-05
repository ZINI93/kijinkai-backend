package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletPort {
    Wallet findByCustomerUuid(UUID customerUuid);
    WalletResponseDto deposit(UUID customerUuid, UUID walletUuid, BigDecimal amount);
    WalletResponseDto withdrawal(UUID customerUuid, UUID walletUuid, BigDecimal amount);
}
