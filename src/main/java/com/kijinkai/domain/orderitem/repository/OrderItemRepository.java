package com.kijinkai.domain.orderitem.repository;

import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderItemUuid(UUID orderUuid);

    Page<OrderItem> findAllByCustomerUuidOrderByOrderCreatedAtDesc(UUID customerUuid, Pageable pageable);

    Page<OrderItem> findAllByCustomerUuidAndOrderItemStatusOrderByOrderCreatedAtDesc(UUID customerUuid, OrderItemStatus status, Pageable pageable);

    List<OrderItem> findByOrderItemUuidInAndCustomerUuid(List<UUID> orderItemUuids, UUID customerUuid);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.customerUuid = :customerUuid AND oi.orderItemStatus = :orderItemStatus ORDER BY oi.createdAt DESC")
    int findOrderItemCountByStatus(@Param("customerUuid") UUID customerUuid, @Param("orderItemStatus") OrderItemStatus orderItemStatus);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.customerUuid = :customerUuid ORDER BY oi.createdAt DESC")
    int findOrderItemCount(@Param("customerUuid") UUID customerUuid);
}

