package com.kijinkai.domain.address.domain.factory;

import com.kijinkai.domain.address.adapter.out.persistence.entity.AddressJpaEntity;
import com.kijinkai.domain.address.application.dto.AddressRequestDto;
import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddressFactory {


    public Address createAddressAndCustomer(UUID customerUuid, CustomerRequestDto requestDto) {

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

    public Address createAddress(UUID customerUuid, AddressRequestDto requestDto) {

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
