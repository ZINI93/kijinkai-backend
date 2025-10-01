package com.kijinkai.domain.address.domain.model;

import com.kijinkai.domain.address.application.dto.AddressUpdateDto;

import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private Long addressId;
    private UUID addressUuid;
    private UUID customerUuid;
    private String recipientName;
    private String recipientPhoneNumber;
    private String country;
    private String zipcode;
    private String state;
    private String city;
    private String street;
    private Boolean isDefault;


    /**
     * 고객 정보 업데이트
     * @param addressUpdateDto
     */
    public void updateAddress(AddressUpdateDto addressUpdateDto) {
        validateUpdateData(addressUpdateDto);

        this.recipientName = addressUpdateDto.getRecipientName();
        this.recipientPhoneNumber = addressUpdateDto.getRecipientPhoneNumber();
        this.country = addressUpdateDto.getCountry();
        this.zipcode = addressUpdateDto.getZipcode();
        this.state = addressUpdateDto.getState();
        this.city = addressUpdateDto.getCity();
        this.street = addressUpdateDto.getStreet();
    }

    private void validateUpdateData(AddressUpdateDto addressUpdateDto){
        if (addressUpdateDto == null){
            throw new IllegalArgumentException("Update data can't be null");
        }
    }
}
