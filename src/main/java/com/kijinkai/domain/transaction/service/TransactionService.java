package com.kijinkai.domain.transaction.service;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.transaction.dto.TransactionResponseDto;
import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.domain.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {

    Transaction createTransactionWithValidate(UUID userUuid, Wallet wallet, Order order, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, TransactionStatus transactionStatus);
    TransactionResponseDto updateTransactionWithValidate(UUID userUuid, UUID transactionUuid);
    TransactionResponseDto getTransactionInfo(UUID userUuid, UUID transactionUuid);
    TransactionResponseDto getTransactionInfoByAdmin(UUID userUuid, UUID transactionUuid);
}
