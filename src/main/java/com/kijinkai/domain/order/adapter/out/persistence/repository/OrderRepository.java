package com.kijinkai.domain.order.adapter.out.persistence.repository;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderJpaEntity, Long> {

    Optional<OrderJpaEntity> findByCustomerUuidAndOrderUuid(UUID customerUuid, UUID orderUuid);
    Optional<OrderJpaEntity> findByOrderUuid(UUID orderUuid);
    Optional<OrderJpaEntity> findByCustomerUuidAndOrderCode(UUID customerUuid, String orderCode);
    List<OrderJpaEntity> findAllByCustomerUuidAndOrderStatusAndIsReviewed(UUID customerUuid, OrderStatus status, boolean isReviewed);
}