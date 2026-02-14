package com.kijinkai.domain.order.application.port.out;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.order.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderPersistencePort {


    Order saveOrder(Order order);
    Optional<Order> findByCustomerUuidAndOrderUuid(UUID customerUuid, UUID orderUuid);
    Optional<Order> findByOrderUuid(UUID orderUuid);
    Optional<Order> findByCustomerUuidAndOrderCode(UUID customerUuid, String orderCode);

    List<Order> findAllByCustomerUuidAndOrderStatusAndIsReviewed(UUID customerUuid, OrderStatus status, boolean isReviewed);
    void deleteOrder(Order order);
}
