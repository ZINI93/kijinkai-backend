package com.kijinkai.domain.order.mapper;

import com.kijinkai.domain.order.dto.OrderRequestDto;
import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.entity.Order;
import org.springframework.stereotype.Component;


@Component
public class OrderMapper {

    public OrderResponseDto toResponse(Order order){

        return OrderResponseDto.builder()
                .orderUuid(order.getOrderUuid())
                .customer(order.getCustomer())
                .finalPriceOriginal(order.getFinalPriceOriginal())
                .memo(order.getMemo())
                .build();
    }
}
