package com.kijinkai.domain.orderitem.adapter.out.persistence.repostiory;

import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItemJpaEntity, Long>, OrderItemRepositoryCustom {

    Optional<OrderItemJpaEntity> findByOrderItemUuid(UUID orderItemUuid);

    Optional<OrderItemJpaEntity> findByOrderUuid(UUID orderUuid);

    Optional<OrderItemJpaEntity> findByCustomerUuidAndOrderItemCode(UUID customerUuid, String orderItemCode);


    Page<OrderItemJpaEntity> findByCustomerUuidOrderByCreatedAtDesc(UUID customerUuid, Pageable pageable);

    Page<OrderItemJpaEntity> findAllByCustomerUuidAndOrderItemStatusOrderByCreatedAtDesc(UUID customerUuid, OrderItemStatus status, Pageable pageable);


    List<OrderItemJpaEntity> findAllByOrderItemStatusAndLocalArrivedAtBefore(OrderItemStatus status, LocalDateTime threshold);

    List<OrderItemJpaEntity> findAllByOrderItemUuidIn(List<UUID> orderItemUuids);

    List<OrderItemJpaEntity> findAllByCustomerUuidAndOrderItemStatusIn(UUID customerUuid, List<OrderItemStatus> orderItemStatuses);

    List<OrderItemJpaEntity> findAllByOrderItemStatusAndOrderItemCodeIn(OrderItemStatus orderItemStatus, List<String> orderItemCode);

    List<OrderItemJpaEntity> findAllByOrderItemCodeInAndOrderItemStatus(List<String> orderItemCode, OrderItemStatus status);

    List<OrderItemJpaEntity> findAllByDeliveryUuid(UUID deliveryUuid);

    List<OrderItemJpaEntity> findAllByCustomerUuidAndOrderItemCodeIn(UUID customerUuid, List<String> orderItemCodes);

    List<OrderItemJpaEntity> findAllByCustomerUuidAndOrderItemStatusAndShipmentUuidIn(UUID customerUuid, OrderItemStatus status, List<UUID> shipmentUuids);

    List<OrderItemJpaEntity> findAllByShipmentUuidAndOrderItemStatus(UUID shipmentUuid, OrderItemStatus status);

    List<OrderItemJpaEntity> findAllByOrderItemCodeIn(List<String> orderItemCode);

    List<OrderItemJpaEntity> findAllByOrderUuid(UUID oderUuid);

    Page<OrderItemJpaEntity> findAllByDeliveryUuid(UUID deliveryUuid, Pageable pageable);

    @Query("SELECT COUNT(oi) FROM OrderItemJpaEntity oi WHERE oi.customerUuid = :customerUuid AND oi.orderItemStatus = :orderItemStatus ORDER BY oi.createdAt DESC")
    int findOrderItemCountByStatus(@Param("customerUuid") UUID customerUuid, @Param("orderItemStatus") OrderItemStatus orderItemStatus);

    @Query("SELECT COUNT(oi) FROM OrderItemJpaEntity oi WHERE oi.customerUuid = :customerUuid AND oi.orderItemStatus IN :orderItemStatus")
    int findOrderItemCountByStatusIn(@Param("customerUuid") UUID customerUuid, @Param("orderItemStatus") List<OrderItemStatus> orderItemStatus);

    @Query("SELECT COUNT(oi) FROM OrderItemJpaEntity oi WHERE oi.customerUuid = :customerUuid ORDER BY oi.createdAt DESC")
    int findOrderItemCount(@Param("customerUuid") UUID customerUuid);

    @Query("SELECT oi.orderUuid as orderUuid, " + // orderUuid로 식별
            "COUNT(oi) as orderItemCount " +
            "FROM OrderItemJpaEntity oi " +
            "WHERE oi.orderUuid IN :orderUuids " + // 사실 orderUuids만 있어도 조회가 가능합니다
            "GROUP BY oi.orderUuid")
    List<CustomerOrderItemSummary> orderItemStatistics(
            @Param("orderUuids") List<UUID> orderUuids // 파라미터명을 쿼리와 일치시킴
    );
}

