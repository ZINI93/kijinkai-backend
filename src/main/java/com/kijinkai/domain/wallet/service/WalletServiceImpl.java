package com.kijinkai.domain.wallet.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.fectory.WalletFactory;
import com.kijinkai.domain.wallet.mapper.WalletMapper;
import com.kijinkai.domain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class WalletServiceImpl implements WalletService{

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final WalletFactory walletFactory;

    @Override
    public WalletResponseDto createWalletWithValidate(Customer customer) {

        Wallet wallet = walletFactory.createWallet(customer);
        Wallet savedWallet = walletRepository.save(wallet);

        return walletMapper.toResponse(savedWallet);
    }
}
