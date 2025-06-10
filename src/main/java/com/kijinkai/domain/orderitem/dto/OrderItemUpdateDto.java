package com.kijinkai.domain.orderitem.dto;

import com.kijinkai.domain.orderitem.entity.Currency;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@Builder
public class OrderItemUpdateDto {

    private String platformUuid;
    private String productLink;
    private int quantity;
    private String memo;
    private BigDecimal priceOriginal;
    private Currency currencyConverted;
}
