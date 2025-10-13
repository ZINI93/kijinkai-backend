package com.kijinkai.domain.orderitem.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@Builder
public class OrderItemUpdateDto {

    private String productLink;
    private int quantity;
    private String memo;
    private BigDecimal priceOriginal;
}
