package com.kijinkai.domain.transaction.service;

import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.transaction.dto.TransactionResponseDto;
import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.wallet.entity.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {

    Transaction createTransactionWithValidate(UUID userUuid, Wallet wallet, Order order, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, TransactionStatus transactionStatus);
    TransactionResponseDto updateTransactionWithValidate(UUID userUuid, UUID transactionUuid);
    TransactionResponseDto getTransactionInfo(UUID userUuid, UUID transactionUuid);
    TransactionResponseDto getTransactionInfoByAdmin(UUID userUuid, UUID transactionUuid);
}
