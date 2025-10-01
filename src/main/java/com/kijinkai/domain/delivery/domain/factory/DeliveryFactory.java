package com.kijinkai.domain.delivery.domain.factory;

import com.kijinkai.domain.address.adapter.out.persistence.entity.AddressJpaEntity;
import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.delivery.application.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.adpater.out.persistence.entity.DeliveryJpaEntity;
import com.kijinkai.domain.delivery.adpater.out.persistence.entity.DeliveryStatus;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class DeliveryFactory {

    public Delivery createDelivery(UUID orderPaymentUuid, UUID customerUuid, Address address, BigDecimal deliveryFee, DeliveryRequestDto requestDto) {

        return Delivery.builder()
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
                .deliveryStatus(DeliveryStatus.SHIPPED)
                .build();
    }
}
