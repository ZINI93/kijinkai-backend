package com.kijinkai.domain.order.adapter.out.persistence.repository;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderJpaEntity, Long> {

    Optional<OrderJpaEntity> findByCustomerUuidAndOrderUuid(UUID customerUuid, UUID orderUuid);
    Optional<OrderJpaEntity> findByOrderUuid(UUID orderUuid);
}