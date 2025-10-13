package com.kijinkai.domain.order.application.dto;

import com.kijinkai.domain.orderitem.application.dto.OrderItemRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class OrderRequestDto {

    private List<OrderItemRequestDto> orderItems;
    private String memo;
    private String convertedCurrency;

    @Builder
    public OrderRequestDto(List<OrderItemRequestDto> orderItems, String memo, String convertedCurrency) {
        this.orderItems = orderItems;
        this.memo = memo;
        this.convertedCurrency = convertedCurrency;
    }
}
