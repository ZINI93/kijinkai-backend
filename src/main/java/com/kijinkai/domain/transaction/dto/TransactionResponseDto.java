package com.kijinkai.domain.transaction.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class TransactionResponseDto {

    public UUID transactionUuid;
    public UUID walletUuid;
    public UUID orderUuid;
    public TransactionType transactionType;
    public BigDecimal amount;
    public BigDecimal balanceBefore;
    public BigDecimal balanceAfter;
    public Currency currency;
    public TransactionStatus transactionStatus;
    public String memo;


    @Builder
    public TransactionResponseDto(UUID transactionUuid, UUID walletUuid, UUID orderUuid, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, Currency currency, TransactionStatus transactionStatus, String memo) {
        this.transactionUuid = transactionUuid;
        this.walletUuid = walletUuid;
        this.orderUuid = orderUuid;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.currency = currency;
        this.transactionStatus = transactionStatus;
        this.memo = memo;
    }
}
