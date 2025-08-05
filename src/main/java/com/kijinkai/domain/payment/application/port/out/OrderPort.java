package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.order.entity.Order;

import java.util.UUID;

public interface OrderPort {

    Order findOrderByOrderUuid(UUID orderUuid);
}
