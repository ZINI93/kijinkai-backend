package com.kijinkai.domain.orderitem.dto;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.entity.Currency;
import com.kijinkai.domain.platform.entity.Platform;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Builder
public class OrderItemResponseDto {

    private String orderItemUuid;
    private String customerUuid;
    private String platformUuid;
    private String orderUuid;
    private String productLink;
    private int quantity;
    private String memo;
    private BigDecimal priceOriginal;
    private BigDecimal priceConverted;
    private String currencyOriginal;
    private Currency currencyConverted;
    private BigDecimal exchangeRate;

    @Builder
    public OrderItemResponseDto(String orderItemUuid, String customerUuid, String platformUuid, String orderUuid, String productLink, int quantity, String memo, BigDecimal priceOriginal, BigDecimal priceConverted, String currencyOriginal, Currency currencyConverted, BigDecimal exchangeRate) {
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
