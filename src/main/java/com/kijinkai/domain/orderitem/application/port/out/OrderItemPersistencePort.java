package com.kijinkai.domain.orderitem.application.port.out;

import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemPersistencePort {

    OrderItem saveOrderItem(OrderItem orderItem);

    List<OrderItem> saveAllOrderItem(List<OrderItem> orderItems);

    void deleteOrderItem(OrderItem orderItem);

    Optional<OrderItem> findByOrderUuid(UUID orderUuid);

    Optional<OrderItem> findByOrderItemUuid(UUID orderUuid);

    List<OrderItem> findAllByDeliveryUuid(UUID deliveryUuid);

    Optional<OrderItem> findByCustomerUuidAndOrderItemCode(UUID customerUuid, String orderItemCode);

    Page<OrderItem> findAllByCustomerUuidOrderByOrderCreatedAtDesc(UUID customerUuid, Pageable pageable);

    Page<OrderItem> findAllByCustomerUuidAndOrderItemStatusOrderByCreatedAtDesc(UUID customerUuid, OrderItemStatus status, Pageable pageable);

    List<OrderItem> findByOrderItemUuidInAndCustomerUuid(List<UUID> orderItemUuids, UUID customerUuid);

    List<OrderItem> findAllByOrderItemUuidIn(List<UUID> orderItemUuids);

    List<OrderItem> findAllByOrderItemStatusAndOrderItemCodeIn(OrderItemStatus status, List<String> orderItemCodes);

    List<OrderItem> findAllByOrderItemCodeInAndOrderItemStatus(List<String> orderItemCode, OrderItemStatus status);

    List<OrderItem> findAllByCustomerUuidAndOrderItemStatusIn(UUID customerUuid, List<OrderItemStatus> orderItemStatuses);

    List<OrderItem> findAllByCustomerUuidAndOrderItemCodeIn(UUID customerUuid, List<String> orderItemCodes);

    List<OrderItem> findAllByCustomerUuidAndOrderItemStatusAndShipmentUuidIn(UUID customerUuid,OrderItemStatus status, List<UUID> shipmentUuids);

    List<OrderItem> findAllByShipmentUuidAndOrderItemStatus(UUID shipmentUuid, OrderItemStatus status);

    int findOrderItemCountByStatus(@Param("customerUuid") UUID customerUuid, @Param("orderItemStatus") OrderItemStatus orderItemStatus);

    int findOrderItemCountByStatusIn(@Param("customerUuid") UUID customerUuid, @Param("orderItemStatus") List<OrderItemStatus> orderItemStatus);

    int findOrderItemCount(@Param("customerUuid") UUID customerUuid);

}
