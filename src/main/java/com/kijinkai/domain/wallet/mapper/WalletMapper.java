package com.kijinkai.domain.wallet.mapper;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.wallet.dto.WalletBalanceResponseDto;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WalletMapper {

    public WalletResponseDto toResponse(Wallet wallet){

        return WalletResponseDto.builder()
                .walletUuid(wallet.getWalletUuid())
                .balance(wallet.getBalance())
                .walletStatus(wallet.getWalletStatus())
                .build();
    }


    public WalletBalanceResponseDto balanceMapper(BigDecimal balance){

        return WalletBalanceResponseDto.builder()
                .balance(balance)
                .build();
    }

}
