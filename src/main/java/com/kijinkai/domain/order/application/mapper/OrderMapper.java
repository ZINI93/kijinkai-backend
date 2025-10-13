package com.kijinkai.domain.order.application.mapper;

import com.kijinkai.domain.order.application.dto.OrderResponseDto;
import com.kijinkai.domain.order.domain.model.Order;
import org.springframework.stereotype.Component;


@Component
public class OrderMapper {

    public OrderResponseDto toResponse(Order order){

        return OrderResponseDto.builder()
                .orderUuid(order.getOrderUuid())
                .customerUuid(order.getCustomerUuid())
                .totalPriceOriginal(order.getTotalPriceOriginal())
                .finalPriceOriginal(order.getFinalPriceOriginal())
                .memo(order.getMemo())
                .build();
    }

}
