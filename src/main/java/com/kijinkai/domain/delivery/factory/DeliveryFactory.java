package com.kijinkai.domain.delivery.factory;

import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.entity.Carrier;
import com.kijinkai.domain.delivery.entity.Delivery;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import com.kijinkai.domain.order.entity.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DeliveryFactory {

    public Delivery createDelivery(Order order, Address address, DeliveryRequestDto requestDto) {

        return new Delivery(
                UUID.randomUUID().toString(),
                order,
                order.getCustomer(),
                DeliveryStatus.PENDING,
                address.getRecipientName(),
                address.getRecipientPhoneNumber(),
                address.getCountry(),
                address.getZipcode(),
                address.getState(),
                address.getCity(),
                address.getStreet(),
                Carrier.YAMATO,
                requestDto.getTrackingNumber(),
                requestDto.getDeliveryFee(),
                LocalDateTime.now().plusDays(20),
                LocalDateTime.now(),
                null,
                null,
                null);

    }
}
