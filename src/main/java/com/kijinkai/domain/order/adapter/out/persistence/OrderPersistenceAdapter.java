package com.kijinkai.domain.order.adapter.out.persistence;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.order.adapter.out.persistence.mapper.OrderPersistenceMapper;
import com.kijinkai.domain.order.adapter.out.persistence.repository.CustomerOrderSummary;
import com.kijinkai.domain.order.adapter.out.persistence.repository.OrderRepository;
import com.kijinkai.domain.order.adapter.out.persistence.repository.OrderSearchCondition;
import com.kijinkai.domain.order.application.port.out.OrderPersistencePort;
import com.kijinkai.domain.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class OrderPersistenceAdapter  implements OrderPersistencePort {


    private final OrderPersistenceMapper orderPersistenceMapper;
    private final OrderRepository orderRepository;

    @Override
    public Order saveOrder(Order order) {
        OrderJpaEntity oderJpaEntity = orderPersistenceMapper.toOderJpaEntity(order);
        oderJpaEntity = orderRepository.save(oderJpaEntity);
        return orderPersistenceMapper.toOrder(oderJpaEntity);
    }

    @Override
    public Optional<Order> findByCustomerUuidAndOrderUuid(UUID customerUuid, UUID orderUuid) {
        return orderRepository.findByCustomerUuidAndOrderUuid(customerUuid,orderUuid)
                .map(orderPersistenceMapper::toOrder);
    }

    @Override
    public Optional<Order> findByOrderUuid(UUID orderUuid) {
        return orderRepository.findByOrderUuid(orderUuid)
                .map(orderPersistenceMapper::toOrder);
    }

    @Override
    public Optional<Order> findByCustomerUuidAndOrderCode(UUID customerUuid, String orderCode) {
        return orderRepository.findByCustomerUuidAndOrderCode(customerUuid, orderCode)
                .map(orderPersistenceMapper::toOrder);
    }

    @Override
    public List<Order> findAllByCustomerUuidAndOrderStatusAndIsReviewed(UUID customerUuid, OrderStatus status, boolean isReviewed) {
        List<OrderJpaEntity> orderJpaEntities = orderRepository.findAllByCustomerUuidAndOrderStatusAndIsReviewed(customerUuid, status, isReviewed);
        return orderPersistenceMapper.toOrders(orderJpaEntities);
    }



    @Override
    public List<CustomerOrderSummary> findOrderStatisticsByCustomerUuids(List<UUID> customerUuids, OrderStatus orderStatus) {
        return orderRepository.findOrderStatisticsByCustomerUuids(customerUuids, orderStatus);
    }

    @Override
    public Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable) {
        return orderRepository.searchOrders(condition, pageable)
                .map(orderPersistenceMapper::toOrder);
    }


    @Override
    public void deleteOrder(Order order) {
        OrderJpaEntity oderJpaEntity = orderPersistenceMapper.toOderJpaEntity(order);
        orderRepository.delete(oderJpaEntity);
    }
}
