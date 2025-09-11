package com.kijinkai.domain.orderitem.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class OrderItemResponseDto {

    UUID orderItemUuid;
    UUID customerUuid;
    UUID orderUuid;
    String productLink;
    int quantity;
    String memo;
    BigDecimal priceOriginal;
    BigDecimal priceConverted;
    String currencyOriginal;
    Currency currencyConverted;
    OrderItemStatus orderItemStatus;
    LocalDateTime createdAt;


    @Builder
    public OrderItemResponseDto(UUID orderItemUuid, UUID customerUuid, UUID orderUuid, String productLink, int quantity, String memo, BigDecimal priceOriginal, BigDecimal priceConverted, String currencyOriginal, Currency currencyConverted, LocalDateTime createdAt, OrderItemStatus orderItemStatus) {
        this.orderItemUuid = orderItemUuid;
        this.customerUuid = customerUuid;
        this.orderUuid = orderUuid;
        this.productLink = productLink;
        this.quantity = quantity;
        this.memo = memo;
        this.priceOriginal = priceOriginal;
        this.priceConverted = priceConverted;
        this.currencyOriginal = currencyOriginal;
        this.currencyConverted = currencyConverted;
        this.createdAt = createdAt;
        this.orderItemStatus = orderItemStatus;

    }
}
