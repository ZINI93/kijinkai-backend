package com.kijinkai.domain.order.repository;

import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByCustomerCustomerUuidAndOrderUuid(String customerUuid, String orderUuid);
    Optional<Order> findByCustomerCustomerIdAndOrderUuid(Long customerId, String orderUuid);
    Optional<Order> findByOrderUuid(String orderUuid);
}