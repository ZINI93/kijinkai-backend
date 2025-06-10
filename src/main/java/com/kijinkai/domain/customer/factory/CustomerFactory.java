package com.kijinkai.domain.customer.factory;

import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CustomerFactory {

    public Customer createCustomer(User user, CustomerRequestDto requestDto){

        return Customer.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .phoneNumber(requestDto.getPhoneNumber())
                .user(user)
                .build();
    }
}
