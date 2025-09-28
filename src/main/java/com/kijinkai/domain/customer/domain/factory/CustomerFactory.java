package com.kijinkai.domain.customer.domain.factory;

import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerFactory {

    public Customer createCustomer(UUID userUuid, CustomerRequestDto requestDto){

        return Customer.builder()
                .customerUuid(UUID.randomUUID())
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .phoneNumber(requestDto.getPhoneNumber())
                .userUuid(userUuid)
                .build();
    }
}
