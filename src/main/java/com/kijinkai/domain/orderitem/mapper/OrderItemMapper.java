package com.kijinkai.domain.orderitem.mapper;


import com.kijinkai.domain.orderitem.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.entity.OrderItem;

public class OrderItemMapper {

    public OrderItemResponseDto toResponseDto(OrderItem orderItem) {

        return OrderItemResponseDto.builder()
                .orderItemUuid(orderItem.getOrderItemUuid())
                .build();
    }






}
