package com.kijinkai.domain.orderitem.adapter.out.persistence;


import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.adapter.out.persistence.mapper.OrderItemPersistenceMapper;
import com.kijinkai.domain.orderitem.adapter.out.persistence.repostiory.OrderItemRepository;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class OrderItemPersistenceAdapter implements OrderItemPersistencePort {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemPersistenceMapper orderItemPersistenceMapper;

    @Override
    public OrderItem saveOrderItem(OrderItem orderItem) {
        OrderItemJpaEntity orderItemJpaEntity = orderItemPersistenceMapper.toOrderItemJpaEntity(orderItem);
        orderItemJpaEntity = orderItemRepository.save(orderItemJpaEntity);
        return orderItemPersistenceMapper.toOrderItem(orderItemJpaEntity);
    }

    @Override
    public List<OrderItem> saveAllOrderItem(List<OrderItem> orderItems) {
        List<OrderItemJpaEntity> orderItemJpaEntities = orderItemPersistenceMapper.toOrderItemsJpaEntity(orderItems);
        orderItemJpaEntities = orderItemRepository.saveAll(orderItemJpaEntities);
        return orderItemPersistenceMapper.toOrderItems(orderItemJpaEntities);
    }

    @Override
    public void deleteOrderItem(OrderItem orderItem) {
        OrderItemJpaEntity orderItemJpaEntity = orderItemPersistenceMapper.toOrderItemJpaEntity(orderItem);
        orderItemRepository.delete(orderItemJpaEntity);
    }

    @Override
    public Optional<OrderItem> findByOrderUuid(UUID orderUuid) {
        return orderItemRepository.findByOrderOrderUuid(orderUuid)
                .map(orderItemPersistenceMapper::toOrderItem);
    }

    @Override
    public Optional<OrderItem> findByOrderItemUuid(UUID orderUuid) {
        return orderItemRepository.findByOrderItemUuid(orderUuid)
                .map(orderItemPersistenceMapper::toOrderItem);
    }

    @Override
    public Page<OrderItem> findAllByCustomerUuidOrderByOrderCreatedAtDesc(UUID customerUuid, Pageable pageable) {
        return orderItemRepository.findAllByCustomerUuidOrderByOrderCreatedAtDesc(customerUuid,pageable)
                .map(orderItemPersistenceMapper::toOrderItem);
    }

    @Override
    public Page<OrderItem> findAllByCustomerUuidAndOrderItemStatusOrderByOrderCreatedAtDesc(UUID customerUuid, OrderItemStatus status, Pageable pageable) {
        return orderItemRepository.findAllByCustomerUuidAndOrderItemStatusOrderByOrderCreatedAtDesc(customerUuid,status,pageable)
                .map(orderItemPersistenceMapper::toOrderItem);
    }

    @Override
    public List<OrderItem> findByOrderItemUuidInAndCustomerUuid(List<UUID> orderItemUuids, UUID customerUuid) {
        return List.of();
    }

    @Override
    public List<OrderItem> findAllByOrderItemUuidIn(List<UUID> orderItemUuids) {
        return orderItemRepository.findAllByOrderItemUuidIn(orderItemUuids)
                .stream().map(orderItemPersistenceMapper::toOrderItem).toList();
    }

    @Override
    public int findOrderItemCountByStatus(UUID customerUuid, OrderItemStatus orderItemStatus) {
        return orderItemRepository.findOrderItemCountByStatus(customerUuid,orderItemStatus);
    }

    @Override
    public int findOrderItemCount(UUID customerUuid) {
        return orderItemRepository.findOrderItemCount(customerUuid);
    }

    @Override
    public List<OrderItem> firstOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID productPaymentUuid) {
        return List.of();
    }

    @Override
    public List<OrderItem> secondOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID deliveryPaymentUuid) {
        return List.of();
    }
}


