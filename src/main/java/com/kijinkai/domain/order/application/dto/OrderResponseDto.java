package com.kijinkai.domain.order.application.dto;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "주문 응답")
public class OrderResponseDto {

    private UUID orderUuid;
    private UUID customerUuid;
    private String orderCode;
    private BigDecimal deliveryFee;
    private BigDecimal totalPriceOriginal;   // 엔화의 상품전체가격
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String convertedCurrency;
    private OrderStatus orderState;
    private String memo;
    private String rejectedReason;
    private PaymentStatus paymentStatus;


}
