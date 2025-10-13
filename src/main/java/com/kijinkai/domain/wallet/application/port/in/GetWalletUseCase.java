package com.kijinkai.domain.wallet.application.port.in;

import com.kijinkai.domain.wallet.application.dto.WalletBalanceResponseDto;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;

import java.util.UUID;

public interface GetWalletUseCase {
    WalletBalanceResponseDto getWalletBalance(UUID userUuid);
    WalletResponseDto getCustomerWalletBalanceByAdmin(UUID userUuid, String walletUuid);
}
