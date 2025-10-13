package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletPort {
    WalletJpaEntity findByCustomerUuid(UUID customerUuid);
    WalletResponseDto deposit(UUID customerUuid, UUID walletUuid, BigDecimal amount);
    WalletResponseDto withdrawal(UUID customerUuid, UUID walletUuid, BigDecimal amount);
}
