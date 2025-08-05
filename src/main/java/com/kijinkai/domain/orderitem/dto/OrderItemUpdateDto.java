package com.kijinkai.domain.orderitem.dto;

import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Builder
public class OrderItemUpdateDto {

    private UUID orderItemUuid;
    private Order order;
    private String platformUuid;
    private String productLink;
    private int quantity;
    private String memo;
    private BigDecimal priceOriginal;
    private Currency currencyConverted;
}
