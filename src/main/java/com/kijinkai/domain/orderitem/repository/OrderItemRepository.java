package com.kijinkai.domain.orderitem.repository;

import com.kijinkai.domain.orderitem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderItemUuid(UUID orderUuid);


    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.order o JOIN FETCH o.customer c WHERE oi.orderItemUuid = :orderItemUuid")
    Optional<OrderItem> findByOrderItemUuidWithOrderAndCustomer(@Param("orderItemUuid") UUID orderItemUuid);}