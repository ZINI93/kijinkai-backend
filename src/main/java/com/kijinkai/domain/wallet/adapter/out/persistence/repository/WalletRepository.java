package com.kijinkai.domain.wallet.adapter.out.persistence.repository;

import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<WalletJpaEntity, Long> {

//    @Modifying
//    @Query("UPDATE WalletJpaEntity w SET w.balance = w.balance + :amount WHERE w.id = :walletId")
//    int increaseBalanceAtomic(@Param("walletId") Long walletId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE WalletJpaEntity w SET w.balance = w.balance + :amount WHERE w.walletUuid = :walletUuid")
    int increaseBalanceAtomic(@Param("walletUuid") UUID walletUuid, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE WalletJpaEntity w SET w.balance = w.balance - :amount WHERE w.walletUuid = :walletUuid AND w.balance >= :amount")
    int decreaseBalanceAtomic(@Param("walletUuid") UUID walletUuid, @Param("amount") BigDecimal amount);
//
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT w FROM WalletJpaEntity w WHERE w.id = :walletId")
//    Optional<WalletJpaEntity> findByIdWithPessimisticLock(@Param("walletId") Long walletId);
//
//    @Query("SELECT w FROM WalletJpaEntity w JOIN FETCH w.customer c WHERE c.customerId = :customerId")
//    Optional<WalletJpaEntity> findByCustomerCustomerIdWithCustomer(@Param("customerId") Long customerId);
//
//    Optional<WalletJpaEntity> findByCustomerCustomerId(Long customerId);

    Optional<WalletJpaEntity> findByWalletId(Long walletId);

    Optional<WalletJpaEntity> findByWalletUuid(UUID walletUuid);

    Optional<WalletJpaEntity> findByCustomerUuidAndWalletUuid(UUID customerUuid, UUID walletUuid);

    Optional<WalletJpaEntity> findByCustomerUuid(UUID customerUuid);



    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WalletJpaEntity w where w.customerUuid = :customerUuid")
    Optional<WalletJpaEntity> findByCustomerUuidWithLock(@Param("customerUuid") UUID customerUuid);



//    /**
//     * 원자적 잔액 증가 (충전용)
//     */
//    @Modifying
//    @Query("UPDATE WalletJpaEntity w SET w.balance = w.balance + :amount, w.updatedAt = CURRENT_TIMESTAMP " +
//            "WHERE w.walletId = :walletId AND w.walletStatus = 'ACTIVE'")
//    int increaseBalanceAtomic(@Param("walletId") Long walletId, @Param("amount") BigDecimal amount);
//
//    /**
//     * 원자적 잔액 감소 (출금용)
//     * 잔액이 충분할 때만 차감
//     */
//    @Modifying
//    @Query("UPDATE WalletJpaEntity w SET w.balance = w.balance - :amount, w.updatedAt = CURRENT_TIMESTAMP " +
//            "WHERE w.walletId = :walletId AND w.walletStatus = 'ACTIVE' AND w.balance >= :amount")
//    int decreaseBalanceAtomic(@Param("walletId") Long walletId, @Param("amount") BigDecimal amount);
//
//    /**
//     * 잔액 조건부 업데이트 (현재 잔액이 예상 잔액과 일치할 때만)
//     */
//    @Modifying
//    @Query("UPDATE WalletJpaEntity w SET w.balance = :newBalance, w.updatedAt = CURRENT_TIMESTAMP " +
//            "WHERE w.walletId = :walletId AND w.balance = :expectedBalance")
//    int updateBalanceIfMatches(@Param("walletId") Long walletId,
//                               @Param("expectedBalance") BigDecimal expectedBalance,
//                               @Param("newBalance") BigDecimal newBalance);
}