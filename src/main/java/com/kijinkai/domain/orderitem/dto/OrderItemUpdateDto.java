package com.kijinkai.domain.orderitem.dto;

import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@Builder
public class OrderItemUpdateDto {

    private String orderItemUuid;
    private Order order;
    private String platformUuid;
    private String productLink;
    private int quantity;
    private String memo;
    private BigDecimal priceOriginal;
    private Currency currencyConverted;
}
