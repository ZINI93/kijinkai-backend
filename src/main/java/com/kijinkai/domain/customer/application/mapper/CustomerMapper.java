package com.kijinkai.domain.customer.application.mapper;

import com.kijinkai.domain.address.adapter.out.persistence.entity.AddressJpaEntity;
import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.customer.application.dto.CustomerCreateResponse;
import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerMapper {

    public CustomerResponseDto toResponse(Customer customer){

        return CustomerResponseDto.builder()
                .customerUuid(customer.getCustomerUuid())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .customerTier(customer.getCustomerTier())
                .userUuid(customer.getUserUuid())
                .build();
    }

}
