package com.kijinkai.domain.transaction.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class TransactionResponseDto {

    public UUID transactionUuid;
    public UUID walletUuid;
    public UUID orderUuid;
    public TransactionType transactionType;
    public BigDecimal amount;
    public Currency currency;
    public TransactionStatus transactionStatus;
    public LocalDateTime updatedAt;
    public LocalDateTime createdAt;
    public String paymentCode;
    public String memo;

}
