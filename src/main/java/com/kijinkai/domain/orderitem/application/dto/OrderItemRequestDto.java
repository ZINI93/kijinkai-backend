package com.kijinkai.domain.orderitem.application.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class OrderItemRequestDto {

    private String productLink;
    private int quantity;
    private String memo;
    private BigDecimal priceOriginal;


    @Builder
    public OrderItemRequestDto(String productLink, int quantity, String memo, BigDecimal priceOriginal) {
        this.productLink = productLink;
        this.quantity = quantity;
        this.memo = memo;
        this.priceOriginal = priceOriginal;
    }
}
