package com.kijinkai.domain.order.service;

import com.kijinkai.domain.order.dto.OrderRequestDto;
import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.dto.OrderUpdateDto;

public interface OrderService {

    OrderResponseDto createOrderProcess(String userUuid, OrderRequestDto requestDto);
    OrderResponseDto completedOrder(String userUuid, String orderUuid);
    OrderResponseDto updateOrderEstimate(String userUuid, String orderUuid, OrderUpdateDto updateDto);
    OrderResponseDto getOrderInfo(String userUuid, String orderUuid);
    OrderResponseDto cancelOrder(String userUuid, String orderUuid);
    OrderResponseDto confirmOrder(String userUuid, String orderUuid);
    void deleteOrder(String userUuid, String orderUuid);

}
