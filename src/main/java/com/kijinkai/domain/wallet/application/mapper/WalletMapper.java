package com.kijinkai.domain.wallet.application.mapper;

import com.kijinkai.domain.wallet.application.dto.WalletBalanceResponseDto;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.domain.model.Wallet;
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
