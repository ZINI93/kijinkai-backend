package com.kijinkai.domain.wallet.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;

public interface WalletService {

    WalletResponseDto createWalletWithValidate(Customer customer);
}
