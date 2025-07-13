package com.kijinkai.domain.wallet.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {

    WalletResponseDto createWalletWithValidate(Customer customer);
    WalletResponseDto deposit(UUID customerUuid, UUID walletUuid, BigDecimal amount);
    WalletResponseDto withdrawal(UUID customerUuid, UUID walletUuid, BigDecimal amount);

}

