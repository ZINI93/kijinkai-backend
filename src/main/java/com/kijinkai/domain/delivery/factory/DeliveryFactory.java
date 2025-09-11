package com.kijinkai.domain.delivery.factory;

import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.entity.Carrier;
import com.kijinkai.domain.delivery.entity.Delivery;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import com.kijinkai.domain.order.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DeliveryFactory {

    public Delivery createDelivery(UUID orderPaymentUuid, Customer customer, Address address, BigDecimal deliveryFee, DeliveryRequestDto requestDto) {

        return Delivery.builder()
                .orderPaymentUuid(orderPaymentUuid)
                .customer(customer)
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
