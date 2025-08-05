package com.kijinkai.domain.order.dto;

import com.kijinkai.domain.order.entity.OrderStatus;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderUpdateDto {

    private OrderStatus orderstate;
    private Currency convertedCurrency;
    private String memo;
    private String rejectedReason;
    private List<OrderItemUpdateDto> orderItems;

    @Builder
    public OrderUpdateDto(OrderStatus orderstate, Currency convertedCurrency, String memo, String rejectedReason, List<OrderItemUpdateDto> orderItems) {
        this.orderstate = orderstate;
        this.convertedCurrency = convertedCurrency;
        this.memo = memo;
        this.rejectedReason = rejectedReason;
        this.orderItems = orderItems;
    }
}
