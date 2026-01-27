package com.kijinkai.domain.order.application.port.in;

import com.kijinkai.domain.order.application.dto.OrderRequestDto;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CreateOrderUseCase {
    OrderResponseDto createOrder(UUID userUuid, OrderRequestDto requestDto);
    OrderResponseDto createAndSaveOrder(UUID customerUuid, List<OrderItem> orderItems, Map<String,Boolean> inspectedPhotoRequest, String orderCode, BigDecimal totalPrice);
}
