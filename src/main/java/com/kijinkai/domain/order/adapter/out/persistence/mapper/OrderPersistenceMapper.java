package com.kijinkai.domain.order.adapter.out.persistence.mapper;


import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.domain.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {

    @Mapping(target = "totalPriceConverted", ignore = true)
    @Mapping(target = "convertedCurrency", ignore = true)
    Order toOrder(OrderJpaEntity orderJpaEntity);

    @Mapping(target = "isReviewed", source = "reviewed")
    OrderJpaEntity toOderJpaEntity(Order order);

    List<Order> toOrders(List<OrderJpaEntity> orderJpaEntities);
    List<OrderJpaEntity> toOrdersJpaEntity(List<Order> orders);


}
