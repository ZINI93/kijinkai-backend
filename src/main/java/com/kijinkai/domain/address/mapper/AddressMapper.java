package com.kijinkai.domain.address.mapper;

import com.kijinkai.domain.address.dto.AddressResponseDto;
import com.kijinkai.domain.address.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressResponseDto toResponse(Address address){

        return AddressResponseDto.builder().
                addressUuid(address.getAddressUuid()).
                customerUuid(address.getCustomerUuid()).
                zipcode(address.getZipcode()).
                state(address.getState()).
                city(address.getCity()).
                street(address.getStreet()).build();


    }
}
