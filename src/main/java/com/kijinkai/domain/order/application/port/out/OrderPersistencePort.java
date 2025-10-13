package com.kijinkai.domain.order.application.port.out;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderPersistencePort {


    Order saveOrder(Order order);
    Optional<Order> findByCustomerUuidAndOrderUuid(UUID customerUuid, UUID orderUuid);
    Optional<Order> findByOrderUuid(UUID orderUuid);
    void deleteOrder(Order order);
}
