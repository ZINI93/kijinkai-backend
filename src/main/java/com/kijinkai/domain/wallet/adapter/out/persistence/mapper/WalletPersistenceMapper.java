package com.kijinkai.domain.wallet.adapter.out.persistence.mapper;

import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletPersistenceMapper {

    Wallet toWallet(WalletJpaEntity walletJpaEntity);
    WalletJpaEntity toWalletJpaEntity(Wallet wallet);

    List<Wallet> toWallets(List<WalletJpaEntity> wallets);
    List<WalletJpaEntity> toWalletsJpaEntity(List<Wallet> wallets);

}
