package com.kijinkai.domain.wallet.application.port.in;

import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.application.dto.WalletFreezeRequest;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.domain.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateWalletUseCase {

    WalletResponseDto deposit(UUID customerUuid, UUID walletUuid, BigDecimal amount);
    WalletResponseDto withdrawal(UUID customerUuid, BigDecimal amount);
    WalletResponseDto freezeWallet(UUID adminUuid, UUID walletUuid, WalletFreezeRequest request);
    WalletResponseDto unFreezeWallet(UUID userUuid, UUID walletUuid);
    Wallet refund(UUID customerUuid, UUID walletUuid, BigDecimal amount);


}
