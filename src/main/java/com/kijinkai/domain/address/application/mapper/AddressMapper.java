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
                recipientName(address.getRecipientName()).
                recipientPhoneNumber(address.getRecipientPhoneNumber()).
                zipcode(address.getZipcode()).
                streetAddress(address.getStreetAddress()).
                detailAddress(address.getDetailAddress()).
                pccc(address.getPccc()).
                build();
    }
}
