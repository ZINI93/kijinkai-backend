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
                .orderUuid(delivery.getOrderPaymentUuid())
                .deliveryStatus(delivery.getDeliveryStatus())
                .recipientPhoneNumber(delivery.getRecipientPhoneNumber())
                .country(delivery.getCountry())
                .zipcode(delivery.getZipcode())
                .state(delivery.getState())
                .city(delivery.getCity())
                .street(delivery.getStreet())
                .carrier(delivery.getCarrier())
                .trackingNumber(delivery.getTrackingNumber())
                .deliveryFee(delivery.getDeliveryFee())
                .estimatedDeliveryAt(delivery.getEstimatedDeliveryAt())
                .shippedAt(delivery.getShippedAt())
                .deliveredAt(delivery.getDeliveredAt())
                .deliveryRequest(delivery.getDeliveryRequest())
                .cancelReason(delivery.getCancelReason())
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
                .recipientPhoneNumber(delivery.getRecipientPhoneNumber())
                .country(delivery.getCountry())
                .zipcode(delivery.getZipcode())
                .state(delivery.getState())
                .city(delivery.getCity())
                .street(delivery.getStreet())
                .carrier(delivery.getCarrier())
                .trackingNumber(delivery.getTrackingNumber())
                .deliveryFee(delivery.getDeliveryFee())
                .estimatedDeliveryAt(delivery.getEstimatedDeliveryAt())
                .shippedAt(delivery.getShippedAt())
                .deliveredAt(delivery.getDeliveredAt())
                .deliveryRequest(delivery.getDeliveryRequest())
                .cancelReason(delivery.getCancelReason())
                .build();
    }

    public DeliveryCountResponseDto deliveryCount(int shippedCount, int deliveredCount){
        return DeliveryCountResponseDto.builder()
                .shippedCount(shippedCount)
                .deliveredCount(deliveredCount)
                .build();
    }
}
