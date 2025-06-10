package com.kijinkai.domain.delivery.mapper;

import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.entity.Delivery;

public class DeliveryMapper {
    public DeliveryResponseDto toResponse(Delivery delivery) {
        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .customerUuid(delivery.getCustomer().getCustomerUuid())
                .receiverName(delivery.getReceiverName())
                .postalCode(delivery.getPostalCode())
                .address1(delivery.getAddress1())
                .address2(delivery.getAddress2())
                .memo(delivery.getMemo())
                .build();
    }
}
