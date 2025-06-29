package com.kijinkai.domain.orderitem.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderItemRequestDto {

    private String platformUuid;
    private String productLink;
    private int quantity;
    private String memo;
    private BigDecimal priceOriginal;
    private Currency currencyConverted;
}
