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
public class TransactionAdminSearchResponseDto {

    public UUID transactionUuid;
    public TransactionStatus transactionStatus;
    public TransactionType transactionType;
    public String paymentCode;
    public String name;
    public String email;
    public BigDecimal amount;
    public LocalDateTime updatedAt;
    public LocalDateTime createdAt;
}
