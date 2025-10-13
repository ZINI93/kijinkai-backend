package com.kijinkai.domain.wallet.application.port.out;

import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface WalletPersistencePort {

    Wallet saveWallet(Wallet wallet);
    void deleteWallet(Wallet wallet);

    @Modifying
    @Query("UPDATE WalletJpaEntity w SET w.balance = w.balance + :amount WHERE w.walletUuid = :walletUuid")
    int increaseBalanceAtomic(@Param("walletUuid") UUID walletUuid, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE WalletJpaEntity w SET w.balance = w.balance - :amount WHERE w.walletUuid = :walletUuid AND w.balance >= :amount")
    int decreaseBalanceAtomic(@Param("walletUuid") UUID walletUuid, @Param("amount") BigDecimal amount);

    Optional<Wallet> findByWalletId(Long walletId);

    Optional<Wallet> findByWalletUuid(UUID walletUuid);

    Optional<Wallet> findByCustomerUuidAndWalletUuid(UUID customerUuid, UUID walletUuid);

    Optional<Wallet> findByCustomerUuid(UUID customerUuid);
}
