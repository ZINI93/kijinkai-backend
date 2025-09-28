package com.kijinkai.domain.delivery.factory;

import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.entity.Delivery;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
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
