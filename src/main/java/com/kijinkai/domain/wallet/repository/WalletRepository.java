package com.kijinkai.domain.wallet.repository;

import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount WHERE w.id = :walletId")
    int increaseBalanceAtomic(@Param("walletId") Long walletId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance - :amount WHERE w.id = :walletId AND w.balance >= :amount")
    int decreaseBalanceAtomic(@Param("walletId") Long walletId, @Param("amount") BigDecimal amount);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :walletId")
    Optional<Wallet> findByIdWithPessimisticLock(@Param("walletId") Long walletId);

    Optional<Wallet> findByCustomerCustomerId(Long customerId);

    Optional<Wallet> findByWalletId(Long walletId);
}