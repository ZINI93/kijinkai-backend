package com.kijinkai.domain.payment.application.dto.response;

import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Value
@Builder
public class OrderPaymentResponseDto {

    UUID paymentUuid;
    String orderPaymentCode;

    UUID customerUuid;
    UUID walletUuid;
    UUID orderUuid;
    BigDecimal paymentAmount;
    OrderPaymentStatus status;
    LocalDateTime paidAt;

    List<String> boxCodes;

    LocalDateTime createAt;
    BigDecimal afterBalance;
    String type;
}
