package com.kijinkai.domain.order.adapter.out.persistence.repository;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderJpaEntity, Long>, OrderRepositoryCustom {

    Optional<OrderJpaEntity> findByCustomerUuidAndOrderUuid(UUID customerUuid, UUID orderUuid);

    Optional<OrderJpaEntity> findByOrderUuid(UUID orderUuid);

    Optional<OrderJpaEntity> findByCustomerUuidAndOrderCode(UUID customerUuid, String orderCode);

    List<OrderJpaEntity> findAllByCustomerUuidAndOrderStatusAndIsReviewed(UUID customerUuid, OrderStatus status, boolean isReviewed);



    @Query("SELECT o.customerUuid AS customerUuid, " +
            "SUM(o.finalPriceOriginal) AS totalAmount, " +
            "COUNT(o) AS orderCount " +
            "FROM OrderJpaEntity o " +
            "WHERE o.customerUuid IN :customerUuids AND o.orderStatus = :orderStatus " +
            "GROUP BY o.customerUuid")
    List<CustomerOrderSummary> findOrderStatisticsByCustomerUuids(
            @Param("customerUuids") List<UUID> customerUuids,
            @Param("orderStatus") OrderStatus orderStatus
    );
}