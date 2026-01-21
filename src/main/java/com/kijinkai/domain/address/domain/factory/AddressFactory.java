package com.kijinkai.domain.address.domain.factory;

import com.kijinkai.domain.address.adapter.out.persistence.entity.AddressJpaEntity;
import com.kijinkai.domain.address.application.dto.AddressRequestDto;
import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddressFactory {

    public Address createAddress(UUID customerUuid, AddressRequestDto requestDto) {

        return Address.builder()
                .addressUuid(UUID.randomUUID())
                .customerUuid(customerUuid)
                .recipientName(requestDto.getRecipientName())
                .recipientPhoneNumber(requestDto.getRecipientPhoneNumber())
                .zipcode(requestDto.getZipcode())
                .streetAddress(requestDto.getStreetAddress())
                .detailAddress(requestDto.getDetailAddress())
                .pccc(requestDto.getPccc())
                .isDefault(true)
                .build();
    }
}
