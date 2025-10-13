package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.application.dto.OrderRequestDto;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;

import java.util.UUID;

public interface OrderPort {

    OrderJpaEntity findOrderByOrderUuid(UUID orderUuid);
    OrderResponseDto createOrderProcess(UUID userUuid, OrderRequestDto requestDto);
}
