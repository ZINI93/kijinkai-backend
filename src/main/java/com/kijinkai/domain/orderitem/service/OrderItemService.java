package com.kijinkai.domain.orderitem.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.entity.OrderItem;

import java.util.List;
import java.util.UUID;

public interface OrderItemService {

    OrderItem createOrderItem(Customer customer, Order order, OrderItemRequestDto requestDto);
    OrderItem updateOrderItemWithValidate(String userUuid,String orderUuid, OrderItemUpdateDto updateDto);
    OrderItem updateOrderItemByAdmin(String userUuid,String orderUuid, OrderItemUpdateDto updateDto);
    OrderItemResponseDto getOrderItemInfo(String userUuid, String orderItemUuid);
    OrderItem findOrderItemByOrderItemUuid(UUID orderItemUuid);

}
