package com.kijinkai.domain.payment.application.dto.response;

import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Value
@Builder
public class OrderPaymentResponseDto {

    UUID paymentUuid;
    UUID customerUuid;
    UUID walletUuid;
    UUID orderUuid;
    BigDecimal paymentAmount;
    OrderPaymentStatus status;
    LocalDateTime paidAt;

    LocalDateTime createAt;
    BigDecimal afterBalance;
    String type;
}
