package com.kijinkai.domain.order.adapter.out.persistence.mapper;


import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.domain.model.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {

    Order toOrder(OrderJpaEntity orderJpaEntity);
    OrderJpaEntity toOderJpaEntity(Order order);

    List<Order> toOrders(List<OrderJpaEntity> orderJpaEntities);
    List<OrderJpaEntity> toOrdersJpaEntity(List<Order> orders);


}
