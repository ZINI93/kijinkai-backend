package com.kijinkai.domain.address.factory;

import com.kijinkai.domain.address.dto.AddressRequestDto;
import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddressFactory {


    public Address createAddress(Customer customer, CustomerRequestDto requestDto) {

        return Address.builder()
                .addressUuid(UUID.randomUUID())
                .customer(customer)
                .recipientName(requestDto.getRecipientName())
                .recipientPhoneNumber(requestDto.getRecipientPhoneNumber())
                .country(requestDto.getCountry())
                .zipcode(requestDto.getZipcode())
                .state(requestDto.getState())
                .city(requestDto.getCity())
                .street(requestDto.getStreet())
                .isDefault(true)
                .build();
    }

    public Address createAddress1(Customer customer, AddressRequestDto requestDto) {

        return Address.builder()
                .addressUuid(UUID.randomUUID())
                .customer(customer)
                .recipientName(requestDto.getRecipientName())           // 추가
                .recipientPhoneNumber(requestDto.getRecipientPhoneNumber()) // 추가
                .country(requestDto.getCountry())                       // 추가
                .zipcode(requestDto.getZipcode())
                .state(requestDto.getState())
                .city(requestDto.getCity())
                .street(requestDto.getStreet())
                .isDefault(true)                                        // 추가
                .build();
    }
}
