package com.kijinkai.domain.wallet.adapter.out.persistence;

import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.adapter.out.persistence.mapper.WalletPersistenceMapper;
import com.kijinkai.domain.wallet.adapter.out.persistence.repository.WalletRepository;
import com.kijinkai.domain.wallet.application.mapper.WalletMapper;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Repository
public class WalletPersistenceAdapter implements WalletPersistencePort {

    private final WalletRepository walletRepository;
    private final WalletPersistenceMapper walletPersistenceMapper;

    @Override
    public Wallet saveWallet(Wallet wallet) {
        WalletJpaEntity walletJpaEntity = walletPersistenceMapper.toWalletJpaEntity(wallet);
        walletJpaEntity = walletRepository.save(walletJpaEntity);
        return walletPersistenceMapper.toWallet(walletJpaEntity);
    }

    @Override
    public void deleteWallet(Wallet wallet) {
        WalletJpaEntity walletJpaEntity = walletPersistenceMapper.toWalletJpaEntity(wallet);
        walletPersistenceMapper.toWallet(walletJpaEntity);
    }

    @Override
    public int increaseBalanceAtomic(UUID walletUuid, BigDecimal amount) {
        return walletRepository.increaseBalanceAtomic(walletUuid,amount);
    }

    @Override
    public int decreaseBalanceAtomic(UUID walletUuid, BigDecimal amount) {
        return walletRepository.decreaseBalanceAtomic(walletUuid,amount);
    }

    @Override
    public Optional<Wallet> findByWalletId(Long walletId) {
        return walletRepository.findByWalletId(walletId)
                .map(walletPersistenceMapper::toWallet);
    }

    @Override
    public Optional<Wallet> findByWalletUuid(UUID walletUuid) {
        return walletRepository.findByWalletUuid(walletUuid)
                .map(walletPersistenceMapper::toWallet);
    }

    @Override
    public Optional<Wallet> findByCustomerUuidAndWalletUuid(UUID customerUuid, UUID walletUuid) {
        return walletRepository.findByCustomerUuidAndWalletUuid(customerUuid,walletUuid)
                .map(walletPersistenceMapper::toWallet);
    }

    @Override
    public Optional<Wallet> findByCustomerUuid(UUID customerUuid) {
        return walletRepository.findByCustomerUuid(customerUuid)
                .map(walletPersistenceMapper::toWallet);

    }
}

