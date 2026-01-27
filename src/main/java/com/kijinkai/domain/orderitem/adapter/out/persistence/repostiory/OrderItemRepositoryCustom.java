package com.kijinkai.domain.orderitem.adapter.out.persistence.repostiory;

import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderItemRepositoryCustom {

    Page<OrderItemJpaEntity> searchAdminOrderItemsByStatus(OrderItemSearchCondition condition,Pageable pageable);

}
