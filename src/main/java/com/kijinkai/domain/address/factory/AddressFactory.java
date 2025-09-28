package com.kijinkai.domain.address.factory;

import com.kijinkai.domain.address.dto.AddressRequestDto;
import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddressFactory {


    public Address createAddress(UUID customerUuid, CustomerRequestDto requestDto) {

        return Address.builder()
                .addressUuid(UUID.randomUUID())
                .customerUuid(customerUuid)
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

    public Address createAddress1(UUID customerUuid, AddressRequestDto requestDto) {

        return Address.builder()
                .addressUuid(UUID.randomUUID())
                .customerUuid(customerUuid)
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
