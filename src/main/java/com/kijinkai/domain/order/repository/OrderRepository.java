package com.kijinkai.domain.order.repository;

import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByCustomerCustomerUuidAndOrderUuid(UUID customerUuid, UUID orderUuid);
    Optional<Order> findByCustomerCustomerIdAndOrderUuid(Long customerId, UUID orderUuid);
    Optional<Order> findByOrderUuid(UUID orderUuid);
}