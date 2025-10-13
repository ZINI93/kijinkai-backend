package com.kijinkai.domain.orderitem.adapter.out.persistence.mapper;

import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemPersistenceMapper {

    OrderItem toOrderItem(OrderItemJpaEntity orderItemJpaEntity);
    OrderItemJpaEntity toOrderItemJpaEntity(OrderItem orderItem);

    List<OrderItem> toOrderItems(List<OrderItemJpaEntity> orderItemJpaEntities);
    List<OrderItemJpaEntity> toOrderItemsJpaEntity(List<OrderItem> orderItems);

}
