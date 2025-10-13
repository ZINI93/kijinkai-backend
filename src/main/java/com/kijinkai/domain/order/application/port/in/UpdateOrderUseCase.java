package com.kijinkai.domain.order.application.port.in;

import com.kijinkai.domain.order.application.dto.OrderResponseDto;
import com.kijinkai.domain.order.application.dto.OrderUpdateDto;

import java.util.UUID;

public interface UpdateOrderUseCase {
    OrderResponseDto completedOrder(UUID userUuid, UUID orderUuid);
    OrderResponseDto updateOrderEstimate(UUID userUuid, UUID orderUuid, OrderUpdateDto updateDto);
    OrderResponseDto cancelOrder(UUID userUuid, UUID orderUuid);
    OrderResponseDto confirmOrder(UUID userUuid, UUID orderUuid);
}
