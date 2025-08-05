package com.kijinkai.domain.wallet.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.wallet.dto.WalletFreezeRequest;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {

    WalletResponseDto createWalletWithValidate(Customer customer);
    WalletResponseDto deposit(UUID customerUuid, UUID walletUuid, BigDecimal amount);
    WalletResponseDto withdrawal(UUID customerUuid, UUID walletUuid, BigDecimal amount);
    WalletResponseDto getWalletBalance(UUID userUuid);
    WalletResponseDto getCustomerWalletBalanceByAdmin(UUID userUuid, String walletUuid);
    WalletResponseDto freezeWallet(UUID adminUUid, String walletUuid, WalletFreezeRequest request);
    WalletResponseDto unFreezeWallet(UUID userUuid, String walletUuid);

    Wallet findByCustomerUuid(UUID customerUuid);
}

