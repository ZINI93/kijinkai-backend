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

    public Delivery createDelivery(UUID customerUuid, Address address, DeliveryRequestDto requestDto) {

        return Delivery.builder()
                .deliveryUuid(UUID.randomUUID())
                .customerUuid(customerUuid)
                .deliveryType(requestDto.getDeliveryType())
                .recipientName(address.getRecipientName())
                .recipientPhoneNumber(address.getRecipientPhoneNumber())
                .zipcode(address.getZipcode())
                .streetAddress(address.getStreetAddress())
                .detailAddress(address.getDetailAddress())
                .deliveryStatus(DeliveryStatus.PENDING)
                .build();
    }
}
