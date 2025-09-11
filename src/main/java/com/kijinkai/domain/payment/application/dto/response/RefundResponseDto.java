package com.kijinkai.domain.payment.application.dto.response;

import com.kijinkai.domain.payment.domain.enums.RefundStatus;
import com.kijinkai.domain.payment.domain.enums.RefundType;
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
public class RefundResponseDto {

    UUID refundUuid;
    UUID customerUuid;
    UUID walletUuid;
    UUID orderItemUuid;
    BigDecimal refundAmount;
    String refundReason;
    RefundType refundType;
    RefundStatus status;
    UUID processedByAdmin;
    LocalDateTime processedAt;
    String adminMemo;

    LocalDateTime createAt;
    BigDecimal afterBalance;
    String type;

}
