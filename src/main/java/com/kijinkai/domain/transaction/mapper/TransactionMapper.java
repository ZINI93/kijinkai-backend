package com.kijinkai.domain.transaction.mapper;

import com.kijinkai.domain.transaction.dto.TransactionResponseDto;
import com.kijinkai.domain.transaction.entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionMapper {

    public TransactionResponseDto toResponse(Transaction transaction){

        return TransactionResponseDto.builder()
                .transactionUuid(transaction.getTransactionUuid())
                .walletUuid(transaction.getWalletUuid())
                .orderUuid(transaction.getOrderUuid())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .currency(transaction.getCurrency())
                .transactionStatus(transaction.getTransactionStatus())
                .build();




    }
}
