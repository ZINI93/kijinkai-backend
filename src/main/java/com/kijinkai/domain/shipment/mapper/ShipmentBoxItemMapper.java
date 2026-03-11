package com.kijinkai.domain.shipment.mapper;

import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.shipment.dto.shipmentBoxItem.ShipmentBoxItemResponseDto;
import com.kijinkai.domain.shipment.entity.ShipmentBoxItemEntity;
import org.springframework.stereotype.Component;

@Component
public class ShipmentBoxItemMapper {

    public ShipmentBoxItemResponseDto toPackedResponse(ShipmentBoxItemEntity shipmentBoxItem, OrderItem orderItem) {

        return ShipmentBoxItemResponseDto.builder()
                .orderItemCode(shipmentBoxItem.getOrderItemCode())
                .productLink(orderItem.getProductLink())
                .quantity(shipmentBoxItem.getQuantity())
                .build();
    }
}
