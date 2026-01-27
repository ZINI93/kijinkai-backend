package com.kijinkai.domain.payment.application.dto.response;


import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class DepositRequestResponseDto {

    private UUID requestUuid;
    private UUID customerUuid;
    private UUID walletUuid;
    private String depositCode;

    private BigDecimal amountOriginal;
    private Currency currencyOriginal;

    private String depositorName;
    private String bankAccount;
    private DepositStatus status;
    private LocalDateTime expiresAt;
    private UUID processedByAdmin;
    private LocalDateTime processedAt;
    private String adminMemo;
    private String rejectionReason;

    private LocalDateTime createAt;
    private BigDecimal afterBalance;
    private String type;
}
