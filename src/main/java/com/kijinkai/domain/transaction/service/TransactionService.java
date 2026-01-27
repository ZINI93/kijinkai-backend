package com.kijinkai.domain.transaction.service;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.transaction.dto.TransactionResponseDto;
import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    Transaction createTransactionWithValidate(UUID userUuid, UUID walletUuid, UUID orderUuid, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, TransactionStatus transactionStatus);

    TransactionResponseDto updateTransactionWithValidate(UUID userUuid, UUID transactionUuid);

    TransactionResponseDto getTransactionInfo(UUID userUuid, UUID transactionUuid);

    TransactionResponseDto getTransactionInfoByAdmin(UUID userUuid, UUID transactionUuid);

    UUID createAccountHistory(UUID customerUuid, UUID walletUuid, TransactionType transactionType, String paymentCode, BigDecimal amount, TransactionStatus transactionStatus);

    List<TransactionResponseDto> getRecentAccountHistoryTopFive(UUID userUuid);

    public void completedPayment(UUID customerUuid, String paymentCode);

    void failedPayment(UUID customerUuid, String paymentCode);

    Page<TransactionResponseDto> getTransactionHistory(UUID userUuid, TransactionType type, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
