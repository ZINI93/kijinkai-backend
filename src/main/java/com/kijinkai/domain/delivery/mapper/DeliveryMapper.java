package com.kijinkai.domain.delivery.mapper;

import com.kijinkai.domain.delivery.dto.DeliveryCountResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.entity.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public DeliveryResponseDto toResponse(Delivery delivery) {
        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .customerUuid(delivery.getCustomerUuid())
                .recipientName(delivery.getRecipientName())
                .build();
    }


    public DeliveryResponseDto searchResponse(Delivery delivery) {
        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .customerUuid(delivery.getCustomerUuid())
                .recipientName(delivery.getRecipientName())
                .trackingNumber(delivery.getTrackingNumber())
                .deliveryStatus(delivery.getDeliveryStatus())
                .build();
    }

    public DeliveryCountResponseDto deliveryCount(int shippedCount, int deliveredCount){
        return DeliveryCountResponseDto.builder()
                .shippedCount(shippedCount)
                .deliveredCount(deliveredCount)
                .build();
    }
}
