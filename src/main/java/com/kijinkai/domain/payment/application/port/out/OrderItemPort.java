package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;

import java.util.List;
import java.util.UUID;

public interface OrderItemPort {

    OrderItem findByOrderItemUuid(UUID orderitemUuid);
    List<OrderItem> firstOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID productPaymentUuid);
    List<OrderItem> secondOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID deliveryPaymentUuid);

}
