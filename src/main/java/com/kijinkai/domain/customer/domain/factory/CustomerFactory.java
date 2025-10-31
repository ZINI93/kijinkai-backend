package com.kijinkai.domain.customer.domain.factory;

import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.customer.domain.model.CustomerTier;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerFactory {

    public Customer createCustomer(UUID userUuid, CustomerRequestDto requestDto){

        validateCreateInput(userUuid, requestDto);

        return Customer.builder()
                .customerUuid(UUID.randomUUID())
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .phoneNumber(requestDto.getPhoneNumber())
                .customerTier(CustomerTier.BRONZE)
                .userUuid(userUuid)
                .build();
    }

    private void validateCreateInput(UUID UserUuid, CustomerRequestDto requestDto){
        if (UserUuid == null){
            throw new IllegalArgumentException("User uuid can't be null");
        }
        if (requestDto == null){
            throw new IllegalArgumentException("Customer request can't be null");
        }

    }
}
