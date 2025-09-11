package com.kijinkai.domain.order.service;

import com.kijinkai.domain.order.dto.OrderRequestDto;
import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.dto.OrderUpdateDto;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;

import java.util.UUID;

public interface OrderService {

    OrderResponseDto createOrderProcess(UUID userUuid, OrderRequestDto requestDto);
    OrderResponseDto completedOrder(UUID userUuid, UUID orderUuid);
    OrderResponseDto updateOrderEstimate(UUID userUuid, UUID orderUuid, OrderUpdateDto updateDto);
    OrderResponseDto getOrderInfo(UUID userUuid, UUID orderUuid);
    OrderResponseDto cancelOrder(UUID userUuid, UUID orderUuid);
    OrderResponseDto confirmOrder(UUID userUuid, UUID orderUuid);
    void deleteOrder(UUID userUuid, UUID orderUuid);
    Order findOrderByOrderUuid(UUID orderUuid);
}
