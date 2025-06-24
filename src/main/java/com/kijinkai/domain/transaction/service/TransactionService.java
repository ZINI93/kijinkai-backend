package com.kijinkai.domain.transaction.service;

import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.transaction.dto.TransactionResponseDto;
import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.wallet.entity.Wallet;

import java.math.BigDecimal;

public interface TransactionService {

    Transaction createTransactionWithValidate(String userUuid, Wallet wallet, Order order, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, TransactionStatus transactionStatus);
    TransactionResponseDto updateTransactionWithValidate(String userUuid, String transactionUuid);
    TransactionResponseDto getTransactionInfo(String userUuid, String transactionUuid);
    TransactionResponseDto getTransactionInfoByAdmin(String userUuid, String transactionUuid);
}
