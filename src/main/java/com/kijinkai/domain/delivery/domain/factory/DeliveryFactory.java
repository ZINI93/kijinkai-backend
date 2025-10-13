package com.kijinkai.domain.delivery.domain.factory;

import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.delivery.application.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class DeliveryFactory {

    public Delivery createDelivery(UUID orderPaymentUuid, UUID customerUuid, Address address, BigDecimal deliveryFee, DeliveryRequestDto requestDto) {

        return Delivery.builder()
                .deliveryUuid(UUID.randomUUID())
                .orderPaymentUuid(orderPaymentUuid)
                .customerUuid(customerUuid)
                .recipientName(address.getRecipientName())
                .recipientPhoneNumber(address.getRecipientPhoneNumber())
                .country(address.getCountry())
                .zipcode(address.getZipcode())
                .state(address.getState())
                .city(address.getCity())
                .street(address.getStreet())
                .deliveryRequest(requestDto.getDeliveryRequest())
                .carrier(requestDto.getCarrier())
                .trackingNumber(requestDto.getTrackingNumber())
                .deliveryFee(deliveryFee)
                .deliveryStatus(DeliveryStatus.PENDING)
                .build();
    }
}
