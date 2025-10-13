package com.kijinkai.domain.orderitem.application.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class OrderItemResponseDto {

    UUID orderItemUuid;
    UUID customerUuid;
    UUID orderUuid;
    String productLink;
    int quantity;
    String memo;
    BigDecimal priceOriginal;
    OrderItemStatus orderItemStatus;
}
