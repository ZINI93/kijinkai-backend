package com.kijinkai.domain.order.adapter.out.persistence.repository;


import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<OrderJpaEntity> searchOrders(OrderSearchCondition condition, Pageable pageable);
}
