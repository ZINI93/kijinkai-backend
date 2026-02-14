package com.kijinkai.domain.order.application.port.in;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;

import java.util.List;
import java.util.UUID;

public interface GetOrderUseCase {

    OrderResponseDto getOrderInfo(UUID userUuid, UUID orderUuid);
    List<OrderResponseDto> getPendingReviewOrders(UUID userUuid);
}
