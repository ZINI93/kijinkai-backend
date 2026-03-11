package com.kijinkai.domain.customer.application.mapper;

import com.kijinkai.domain.address.adapter.out.persistence.entity.AddressJpaEntity;
import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.customer.application.dto.CustomerCreateResponse;
import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.user.domain.model.User;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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



    public CustomerResponseDto toUserListResponse(Customer customer, User user, Long totalOrderCount, BigDecimal totalOrderAmount){

        return CustomerResponseDto.builder()
                .customerUuid(customer.getCustomerUuid())
                .fullName(customer.getLastName() + customer.getFirstName())
                .email(user.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .totalOrderCount(totalOrderCount)
                .totalOrderAmount(totalOrderAmount)
                .userStatus(user.getUserStatus())
                .createAt(user.getCreatedAt().toLocalDate())
                .customerTier(customer.getCustomerTier())
                .build();
    }

}
