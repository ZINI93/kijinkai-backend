package com.kijinkai.domain.customer.mapper;

import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponseDto toResponse(Customer customer){

        return CustomerResponseDto.builder()
                .customerUuid(customer.getCustomerUuid())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .customerTier(customer.getCustomerTier())
                .userUuid(customer.getUser().getUserUuid())
                .build();
    }
}
