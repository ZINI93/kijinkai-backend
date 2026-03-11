package com.kijinkai.domain.orderitem.application.mapper;


import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.application.dto.OrderItemCountResponseDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;

import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.user.domain.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemMapper {


    public OrderItemResponseDto toRejectResponse(OrderItem orderItem) {

        return OrderItemResponseDto.builder()
                .orderItemUuid(orderItem.getOrderItemUuid())
                .rejectReason(orderItem.getRejectReason())
                .build();
    }

    public OrderItemResponseDto toResponseDto(OrderItem orderItem) {

        return OrderItemResponseDto.builder()
                .orderItemUuid(orderItem.getOrderItemUuid())
                .customerUuid(orderItem.getCustomerUuid())
                .productLink(orderItem.getProductLink())
                .priceOriginal(orderItem.getPriceOriginal())
                .quantity(orderItem.getQuantity())
                .memo(orderItem.getMemo())
                .orderItemStatus(orderItem.getOrderItemStatus())
                .createdAt(orderItem.getCreatedAt().toLocalDate())
                .build();
    }

    public OrderItemResponseDto toOrderItemUuidResponseDto(OrderItem orderItem) {

        return OrderItemResponseDto.builder()
                .orderItemUuid(orderItem.getOrderItemUuid())
                .build();
    }


    public OrderItemResponseDto toProductDetailDto(OrderItem orderItem) {

        return OrderItemResponseDto.builder()
                .orderItemUuid(orderItem.getOrderItemUuid())
                .orderItemCode(orderItem.getOrderItemCode())
                .productLink(orderItem.getProductLink())
                .priceOriginal(orderItem.getPriceOriginal())
                .quantity(orderItem.getQuantity())
                .memo(orderItem.getMemo())
                .createdAt(orderItem.getCreatedAt().toLocalDate())
                .rejectReason(orderItem.getRejectReason())
                .build();
    }

    public OrderItemResponseDto toDetailOrderItemDto(OrderItem orderItem) {

        return OrderItemResponseDto.builder()
                .orderItemCode(orderItem.getOrderItemCode())
                .productLink(orderItem.getProductLink())
                .priceOriginal(orderItem.getPriceOriginal())
                .quantity(orderItem.getQuantity())
                .build();
    }


    public OrderItemResponseDto toRequestDeliveryResponse(OrderItem orderItem) {
        return OrderItemResponseDto.builder()
                .orderItemUuid(orderItem.getOrderItemUuid())
                .orderItemCode(orderItem.getOrderItemCode())
                .productLink(orderItem.getProductLink())
                .quantity(orderItem.getQuantity())
                .memo(orderItem.getMemo())
                .build();
    }

    public OrderItemResponseDto teOrderItemListResponse(User user, Customer customer, OrderItem orderItem) {

        return OrderItemResponseDto.builder()
                .orderItemUuid(orderItem.getOrderItemUuid())
                .email(user.getEmail())
                .name(customer.getLastName() + customer.getFirstName())
                .orderItemStatus(orderItem.getOrderItemStatus())
                .orderItemCode(orderItem.getOrderItemCode())
                .productLink(orderItem.getProductLink())
                .priceOriginal(orderItem.getPriceOriginal())
                .quantity(orderItem.getQuantity())
                .rejectReason(orderItem.getRejectReason())
                .createdAt(orderItem.getCreatedAt().toLocalDate())
                .updatedAt(orderItem.getUpdatedAt().toLocalDate())
                .memo(orderItem.getMemo())
                .inspectionStatus(orderItem.getInspectionStatus())
                .build();
    }


    public OrderItemCountResponseDto orderItemDashboardCount(
            int allCount, int pendingCount, int pendingApprovalCount
    ) {

        return OrderItemCountResponseDto.builder()
                .allOrderItemCount(allCount)
                .pendingCount(pendingCount)
                .pendingApprovalCount(pendingApprovalCount)
                .build();

    }




}
