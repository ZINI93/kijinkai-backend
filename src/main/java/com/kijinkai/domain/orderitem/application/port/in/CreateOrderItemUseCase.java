package com.kijinkai.domain.orderitem.application.port.in;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.application.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;

import java.util.List;
import java.util.UUID;

public interface CreateOrderItemUseCase {

    OrderItem createOrderItem(Customer customer, Order order, OrderItemRequestDto requestDto);

    List<OrderItem> secondOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID deliveryPaymentUuid);
}
