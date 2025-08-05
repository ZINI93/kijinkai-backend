package com.kijinkai.domain.payment.application.dto.request;

import com.kijinkai.domain.payment.domain.enums.RefundStatus;
import com.kijinkai.domain.payment.domain.enums.RefundType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class RefundRequestDto {

    String refundReason;
    RefundType refundType;
    String adminMemo;
}
