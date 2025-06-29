package com.kijinkai.domain.delivery.mapper;

import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.entity.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public DeliveryResponseDto toResponse(Delivery delivery) {
        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .customerUuid(delivery.getCustomer().getCustomerUuid())
                .recipientName(delivery.getRecipientName())
                .build();
    }
}
