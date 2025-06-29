package com.kijinkai.domain.orderitem.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Builder
public class OrderItemResponseDto {

    private UUID orderItemUuid;
    private UUID customerUuid;
    private UUID platformUuid;
    private UUID orderUuid;
    private String productLink;
    private int quantity;
    private String memo;
    private BigDecimal priceOriginal;
    private BigDecimal priceConverted;
    private String currencyOriginal;
    private Currency currencyConverted;
    private BigDecimal exchangeRate;

    @Builder
    public OrderItemResponseDto(UUID orderItemUuid, UUID customerUuid, UUID platformUuid, UUID orderUuid, String productLink, int quantity, String memo, BigDecimal priceOriginal, BigDecimal priceConverted, String currencyOriginal, Currency currencyConverted, BigDecimal exchangeRate) {
        this.orderItemUuid = orderItemUuid;
        this.customerUuid = customerUuid;
        this.platformUuid = platformUuid;
        this.orderUuid = orderUuid;
        this.productLink = productLink;
        this.quantity = quantity;
        this.memo = memo;
        this.priceOriginal = priceOriginal;
        this.priceConverted = priceConverted;
        this.currencyOriginal = currencyOriginal;
        this.currencyConverted = currencyConverted;
        this.exchangeRate = exchangeRate;
    }
}
