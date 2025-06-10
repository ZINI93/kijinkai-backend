package com.kijinkai.domain.delivery.factory;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.entity.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryFactory {

    public Delivery createDelivery(Customer customer, DeliveryRequestDto requestDto){

        return Delivery.builder()
                .customer(customer)
                .receiverName(requestDto.getReceiverName())
                .postalCode(requestDto.getPostalCode())
                .address1(requestDto.getAddress1())
                .address2(requestDto.getAddress2())
                .memo(requestDto.getMemo())
                .build();
    }
}
