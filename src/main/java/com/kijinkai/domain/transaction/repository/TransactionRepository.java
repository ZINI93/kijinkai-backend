
package com.kijinkai.domain.transaction.repository;

import com.kijinkai.domain.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByCustomerUuidAndTransactionUuid(UUID customerUuid, UUID transactionUuid);
    Optional<Transaction> findByTransactionUuid(UUID transactionUuid);
}