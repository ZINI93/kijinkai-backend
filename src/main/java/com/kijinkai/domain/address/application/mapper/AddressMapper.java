package com.kijinkai.domain.address.application.mapper;

import com.kijinkai.domain.address.application.dto.AddressResponseDto;
import com.kijinkai.domain.address.adapter.out.persistence.entity.AddressJpaEntity;
import com.kijinkai.domain.address.domain.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressResponseDto toResponse(Address address) {

        return AddressResponseDto.builder().
                addressUuid(address.getAddressUuid()).
                customerUuid(address.getCustomerUuid()).
                recipientName(address.getRecipientName()).
                recipientPhoneNumber(address.getRecipientPhoneNumber()).
                country(address.getCountry()).
                zipcode(address.getZipcode()).
                state(address.getState()).
                city(address.getCity()).
                street(address.getStreet())
                .build();
    }
}
