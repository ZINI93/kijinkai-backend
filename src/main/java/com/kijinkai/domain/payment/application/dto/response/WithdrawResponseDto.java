package com.kijinkai.domain.payment.application.dto.response;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class WithdrawResponseDto {

    UUID requestUuid;
    UUID customerUuid;
    UUID walletUuid;
    BigDecimal requestAmount;
    BigDecimal withdrawFee;
    BigDecimal totalDeductAmount;
    Currency targetCurrency;
    BigDecimal convertedAmount;
    BigDecimal exchangeRate;
    String bankName;
    String accountNumber;
    WithdrawStatus status;
    String accountHolder;
    UUID processedByAdmin;
    LocalDateTime processedAt;
    String adminMemo;
    String rejectionReason;
}
