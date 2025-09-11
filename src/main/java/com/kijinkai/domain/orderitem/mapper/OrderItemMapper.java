package com.kijinkai.domain.orderitem.mapper;


import com.kijinkai.domain.orderitem.dto.OrderItemCountResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemResponseDto toResponseDto(OrderItem orderItem) {

        return OrderItemResponseDto.builder()
                .createdAt(orderItem.getCreatedAt())
                .orderItemUuid(orderItem.getOrderItemUuid())
                .customerUuid(orderItem.getCustomerUuid())
                .productLink(orderItem.getProductLink())
                .priceOriginal(orderItem.getPriceOriginal())
                .quantity(orderItem.getQuantity())
                .memo(orderItem.getMemo())
                .orderItemStatus(orderItem.getOrderItemStatus())
                .build();
    }


    public OrderItemCountResponseDto orderItemDashboardCount(
            int allCount, int pendingCount, int pendingApprovalCount
    ){

        return OrderItemCountResponseDto.builder()
                .allOrderItemCount(allCount)
                .pendingCount(pendingCount)
                .pendingApprovalCount(pendingApprovalCount)
                .build();

    }





}
