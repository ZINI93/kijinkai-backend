package com.kijinkai.domain.orderitem.application.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class OrderItemRequestDto {

    private String productLink;
    private int quantity;
    private String memo;
    private BigDecimal priceOriginal;
    private String rejectReason;

}
