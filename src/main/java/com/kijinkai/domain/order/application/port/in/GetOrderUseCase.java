package com.kijinkai.domain.order.application.port.in;

import com.kijinkai.domain.order.application.dto.OrderResponseDto;

import java.util.UUID;

public interface GetOrderUseCase {

    OrderResponseDto getOrderInfo(UUID userUuid, UUID orderUuid);
}
