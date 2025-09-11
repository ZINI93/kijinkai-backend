package com.kijinkai.domain.orderitem.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class OrderItemRequestDto {

    private UUID orderItemUuid;
    private String productLink;
    private int quantity;
    private String memo;
    private BigDecimal priceOriginal;
    private Currency currencyConverted;


    @Builder
    public OrderItemRequestDto(UUID orderItemUuid, String productLink, int quantity, String memo, BigDecimal priceOriginal, Currency currencyConverted) {
        this.orderItemUuid = orderItemUuid;
        this.productLink = productLink;
        this.quantity = quantity;
        this.memo = memo;
        this.priceOriginal = priceOriginal;
        this.currencyConverted = currencyConverted;
    }
}
