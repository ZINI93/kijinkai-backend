package com.kijinkai.domain.orderitem.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderItemService {

    OrderItem createOrderItemProcess(Customer customer, Order order, OrderItemRequestDto requestDto);
    OrderItem updateOrderItemWithValidate(String orderUuid, OrderItemUpdateDto updateDto);
    OrderItemResponseDto getOrderItemInfo(String userUuid, String orderItemUuid);
}
