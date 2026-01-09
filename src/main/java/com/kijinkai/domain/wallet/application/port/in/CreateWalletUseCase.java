package com.kijinkai.domain.wallet.application.port.in;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.domain.model.Wallet;

import java.util.UUID;

public interface CreateWalletUseCase {
    Wallet createWallet(UUID customerUuid);
}
