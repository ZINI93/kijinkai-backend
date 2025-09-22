package com.kijinkai.domain.customer.factory;

import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.kijinkai.domain.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class CustomerFactory {

    public Customer createCustomer(UserJpaEntity user, CustomerRequestDto requestDto){

        return Customer.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .phoneNumber(requestDto.getPhoneNumber())
                .user(user)
                .build();
    }
}
