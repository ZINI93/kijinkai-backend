package com.kijinkai.domain.order.service;

import com.kijinkai.domain.order.dto.OrderRequestDto;
import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.dto.OrderUpdateDto;
import com.kijinkai.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponseDto createOrderProcess(String userUuid, OrderRequestDto requestDto);
    OrderResponseDto updateOrderWithValidate(String userUuid, String orderUuid, OrderUpdateDto updateDto);
    OrderResponseDto getOrderInfo(String userUuid, String orderUuid);
    OrderResponseDto cancelOrder(String userUuid, String orderUuid);
    OrderResponseDto confirmOrder(String userUuid, String orderUuid);
    void deleteOrder(String userUuid, String orderUuid);

}
