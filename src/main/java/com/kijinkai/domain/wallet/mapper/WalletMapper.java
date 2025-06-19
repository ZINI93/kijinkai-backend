package com.kijinkai.domain.wallet.mapper;

import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;

public class WalletMapper {


    public WalletResponseDto toResponse(Wallet wallet){

        return WalletResponseDto.builder()
                .walletUuid(wallet.getWalletUuid())
                .balance(wallet.getBalance())
                .walletStatus(wallet.getWalletStatus())
                .build();
    }
}
