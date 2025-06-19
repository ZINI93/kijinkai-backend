package com.kijinkai.domain.address.fectory;

import com.kijinkai.domain.address.dto.AddressRequestDto;
import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.customer.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddressFactory {


    public Address createAddress(Customer customer, AddressRequestDto requestDto){


        return Address.builder().
                addressUuid(UUID.randomUUID().toString()).
                customer(customer).
                zipcode(requestDto.getZipcode()).
                state(requestDto.getState()).
                city(requestDto.getCity()).
                street(requestDto.getStreet())
                .build();
    }
}
