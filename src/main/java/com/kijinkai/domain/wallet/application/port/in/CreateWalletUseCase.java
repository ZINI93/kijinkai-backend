package com.kijinkai.domain.wallet.application.port.in;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;

public interface CreateWalletUseCase {
    WalletResponseDto createWallet(Customer customer);
}
