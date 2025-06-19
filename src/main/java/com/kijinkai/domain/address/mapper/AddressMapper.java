package com.kijinkai.domain.address.mapper;

import com.kijinkai.domain.address.dto.AddressResponseDto;
import com.kijinkai.domain.address.entity.Address;

public class AddressMapper {


    public AddressResponseDto toResponse(Address address){

        return AddressResponseDto.builder().
                addressUuid(address.getAddressUuid()).
                customerUuid(address.getCustomer().getCustomerUuid()).
                zipcode(address.getZipcode()).
                state(address.getState()).
                city(address.getCity()).
                street(address.getStreet()).build();


    }
}
