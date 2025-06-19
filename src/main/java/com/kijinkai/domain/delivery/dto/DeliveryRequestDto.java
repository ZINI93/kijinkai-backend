package com.kijinkai.domain.delivery.dto;

import com.kijinkai.domain.delivery.entity.Carrier;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class DeliveryRequestDto {

    private Carrier carrier;
    private String trackingNumber;
    private BigDecimal deliveryFee;
    private String deliveryRequest; // 배송 요청 사항
}
