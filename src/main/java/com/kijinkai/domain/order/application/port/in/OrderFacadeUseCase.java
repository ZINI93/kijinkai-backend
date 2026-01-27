package com.kijinkai.domain.order.application.port.in;

import com.kijinkai.domain.order.application.dto.OrderRequestDto;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;

import java.util.UUID;

public interface OrderFacadeUseCase {

    OrderResponseDto completedOrder(UUID userUuid, OrderRequestDto requestDto);
}
