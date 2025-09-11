package com.kijinkai.domain.payment.infrastructure.adapter.out.external;

import com.kijinkai.domain.order.dto.OrderRequestDto;
import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.service.OrderService;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.port.out.OrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class OrderAdapter implements OrderPort {

    private final OrderService orderService;

    @Override
    public Order findOrderByOrderUuid(UUID orderUuid) {
        return orderService.findOrderByOrderUuid(orderUuid);
    }

    @Override
    public OrderResponseDto createOrderProcess(UUID userUuid, OrderRequestDto requestDto) {
        return orderService.createOrderProcess(userUuid, requestDto);
    }
}
