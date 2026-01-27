
package com.kijinkai.domain.transaction.repository;

import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.transaction.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {

    Optional<Transaction> findByCustomerUuidAndTransactionUuid(UUID customerUuid, UUID transactionUuid);
    Optional<Transaction> findByTransactionUuid(UUID transactionUuid);
    Optional<Transaction> findByCustomerUuidAndPaymentCode(UUID customerUuid, String paymentCode);

    List<Transaction> findTop5ByCustomerUuidAndTransactionTypeInOrderByCreatedAtDesc(UUID customerUuid, List<TransactionType> transactionTypes);

}