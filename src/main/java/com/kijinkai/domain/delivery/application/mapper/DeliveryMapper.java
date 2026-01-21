package com.kijinkai.domain.delivery.application.mapper;

import com.kijinkai.domain.delivery.application.dto.DeliveryCountResponseDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.adpater.out.persistence.entity.DeliveryJpaEntity;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {


    public DeliveryResponseDto toResponse(Delivery delivery) {
        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .customerUuid(delivery.getCustomerUuid())
                .recipientName(delivery.getRecipientName())
                .deliveryStatus(delivery.getDeliveryStatus())
                .recipientPhoneNumber(delivery.getRecipientPhoneNumber())
                .zipcode(delivery.getZipcode())
                .streetAddress(delivery.getStreetAddress())
                .detailAddress(delivery.getDetailAddress())
                .trackingNumber(delivery.getTrackingNumber())
                .deliveryRequest(delivery.getDeliveryRequest())
                .build();
    }


    public DeliveryResponseDto searchResponse(Delivery delivery) {
        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .customerUuid(delivery.getCustomerUuid())
                .recipientName(delivery.getRecipientName())
                .trackingNumber(delivery.getTrackingNumber())
                .deliveryStatus(delivery.getDeliveryStatus())
                .deliveryStatus(delivery.getDeliveryStatus())
                .recipientPhoneNumber(delivery.getRecipientPhoneNumber()).zipcode(delivery.getZipcode())
                .trackingNumber(delivery.getTrackingNumber())
                .deliveryRequest(delivery.getDeliveryRequest())
                .build();
    }

    public DeliveryCountResponseDto deliveryCount(int shippedCount, int deliveredCount){
        return DeliveryCountResponseDto.builder()
                .shippedCount(shippedCount)
                .deliveredCount(deliveredCount)
                .build();
    }
}
