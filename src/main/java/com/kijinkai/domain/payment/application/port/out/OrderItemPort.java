package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.orderitem.entity.OrderItem;

import java.util.UUID;

public interface OrderItemPort {

    OrderItem findByOrderItemUuid(UUID orderitemUuid);
}
