package com.kijinkai.domain.wallet.application.port.in;

import com.kijinkai.domain.wallet.application.dto.WalletFreezeRequest;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateWalletUseCase {

    WalletResponseDto deposit(UUID customerUuid, UUID walletUuid, BigDecimal amount);
    WalletResponseDto withdrawal(UUID customerUuid, UUID walletUuid, BigDecimal amount);
    WalletResponseDto freezeWallet(UUID adminUUid, UUID walletUuid, WalletFreezeRequest request);
    WalletResponseDto unFreezeWallet(UUID userUuid, UUID walletUuid);
}
