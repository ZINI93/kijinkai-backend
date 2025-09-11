package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.order.dto.OrderRequestDto;
import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;

import java.util.UUID;

public interface OrderPort {

    Order findOrderByOrderUuid(UUID orderUuid);
    OrderResponseDto createOrderProcess(UUID userUuid, OrderRequestDto requestDto);
}
