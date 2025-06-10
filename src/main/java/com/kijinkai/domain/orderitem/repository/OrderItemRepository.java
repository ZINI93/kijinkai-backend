package com.kijinkai.domain.orderitem.repository;

import com.kijinkai.domain.orderitem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderUuid(String orderUuid);
    Optional<OrderItem> findByCustomerUuidAndOrderItemUuid(String customerUuid, String orderItemUuid);
}