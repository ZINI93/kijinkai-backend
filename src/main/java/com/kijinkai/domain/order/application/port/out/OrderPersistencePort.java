package com.kijinkai.domain.order.application.port.out;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.order.adapter.out.persistence.repository.CustomerOrderSummary;
import com.kijinkai.domain.order.adapter.out.persistence.repository.OrderSearchCondition;
import com.kijinkai.domain.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderPersistencePort {

    Order saveOrder(Order order);

    Optional<Order> findByCustomerUuidAndOrderUuid(UUID customerUuid, UUID orderUuid);

    Optional<Order> findByOrderUuid(UUID orderUuid);

    Optional<Order> findByCustomerUuidAndOrderCode(UUID customerUuid, String orderCode);

    List<Order> findAllByCustomerUuidAndOrderStatusAndIsReviewed(UUID customerUuid, OrderStatus status, boolean isReviewed);

    List<CustomerOrderSummary> findOrderStatisticsByCustomerUuids(@Param("customerUuids") List<UUID> customerUuids, @Param("orderStatus") OrderStatus orderStatus);

    Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable);

    void deleteOrder(Order order);



}
