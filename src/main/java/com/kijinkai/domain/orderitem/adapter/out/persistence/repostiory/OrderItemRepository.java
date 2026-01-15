package com.kijinkai.domain.orderitem.adapter.out.persistence.repostiory;

import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItemJpaEntity, Long> {

    Optional<OrderItemJpaEntity> findByOrderItemUuid(UUID orderItemUuid);
    Optional<OrderItemJpaEntity> findByOrderUuid(UUID orderUuid);
    Optional<OrderItemJpaEntity> findByCustomerUuidAndOrderItemCode(UUID customerUuid, String orderItemCode);


    Page<OrderItemJpaEntity> findByCustomerUuidOrderByCreatedAtDesc(UUID customerUuid, Pageable pageable);
    Page<OrderItemJpaEntity> findAllByCustomerUuidAndOrderItemStatusOrderByCreatedAtDesc(UUID customerUuid, OrderItemStatus status, Pageable pageable);


    List<OrderItemJpaEntity> findAllByOrderItemUuidIn(List<UUID> orderItemUuids);
    List<OrderItemJpaEntity> findAllByCustomerUuidAndOrderItemStatusIn(UUID customerUuid, List<OrderItemStatus> orderItemStatuses);
    List<OrderItemJpaEntity> findAllByOrderItemStatusAndOrderItemCodeIn(OrderItemStatus orderItemStatus, List<String> orderItemCode);
    List<OrderItemJpaEntity> findAllByOrderItemCodeInAndOrderItemStatus(List<String> orderItemCode, OrderItemStatus status);


    @Query("SELECT COUNT(oi) FROM OrderItemJpaEntity oi WHERE oi.customerUuid = :customerUuid AND oi.orderItemStatus = :orderItemStatus ORDER BY oi.createdAt DESC")
    int findOrderItemCountByStatus(@Param("customerUuid") UUID customerUuid, @Param("orderItemStatus") OrderItemStatus orderItemStatus);

    @Query("SELECT COUNT(oi) FROM OrderItemJpaEntity oi WHERE oi.customerUuid = :customerUuid AND oi.orderItemStatus IN :statuses")
    int findOrderItemCountByStatusIn(@Param("customerUuid") UUID customerUuid, @Param("statuses") List<OrderItemStatus> orderItemStatus);

    @Query("SELECT COUNT(oi) FROM OrderItemJpaEntity oi WHERE oi.customerUuid = :customerUuid ORDER BY oi.createdAt DESC")
    int findOrderItemCount(@Param("customerUuid") UUID customerUuid);
}

