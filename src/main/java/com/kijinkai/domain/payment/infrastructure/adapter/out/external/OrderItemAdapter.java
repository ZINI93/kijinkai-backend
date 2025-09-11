package com.kijinkai.domain.payment.infrastructure.adapter.out.external;

import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.service.OrderItemService;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.port.out.OrderItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class OrderItemAdapter implements OrderItemPort{

    private final OrderItemService orderItemService;

    @Override
    public OrderItem findByOrderItemUuid(UUID orderItemUuid){
        return orderItemService.findOrderItemByOrderItemUuid(orderItemUuid);
    }

    @Override
    public List<OrderItem> firstOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID productPaymentUuid) {
        return orderItemService.firstOrderItemPayment(customerUuid, request,productPaymentUuid);
    }

    @Override
    public List<OrderItem> secondOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID deliveryPaymentUuid) {
        return orderItemService.secondOrderItemPayment(customerUuid,request, deliveryPaymentUuid);
    }

}
