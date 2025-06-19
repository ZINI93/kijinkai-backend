package com.kijinkai.domain.order.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
public class OrderItemEstimateDto {

    private String orderItemUuid; // 견적을 낼 OrderItem의 UUID
    private BigDecimal estimatedPrice; // 해당 OrderItem의 견적 가격

    public OrderItemEstimateDto(String orderItemUuid, BigDecimal estimatedPrice) {
        this.orderItemUuid = orderItemUuid;
        this.estimatedPrice = estimatedPrice;
    }
}
