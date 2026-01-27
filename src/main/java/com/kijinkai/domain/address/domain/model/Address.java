package com.kijinkai.domain.address.domain.model;

import com.kijinkai.domain.address.application.dto.AddressUpdateDto;

import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;
import lombok.*;

import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private Long addressId;
    private UUID addressUuid;
    private UUID customerUuid;
    private String recipientName;
    private String recipientPhoneNumber;
    private String zipcode;
    private String streetAddress;
    private String detailAddress;

    private Boolean isDefault;


    /**
     * 고객 정보 업데이트
     * @param addressUpdateDto
     */
    public void updateAddress(AddressUpdateDto addressUpdateDto) {
        validateUpdateData(addressUpdateDto);

        this.recipientName = addressUpdateDto.getRecipientName() != null ? addressUpdateDto.getRecipientName() : this.recipientName;
        this.recipientPhoneNumber = addressUpdateDto.getRecipientPhoneNumber() != null ? addressUpdateDto.getRecipientPhoneNumber() : this.recipientPhoneNumber;
        this.zipcode = addressUpdateDto.getZipcode() != null ? addressUpdateDto.getZipcode() : this.zipcode;
        this.streetAddress = addressUpdateDto.getStreetAddress() != null ? addressUpdateDto.getStreetAddress() : this.streetAddress;
        this.detailAddress = addressUpdateDto.getDetailAddress() != null ? addressUpdateDto.getDetailAddress() : this.detailAddress;
    }

    private void validateUpdateData(AddressUpdateDto addressUpdateDto){
        if (addressUpdateDto == null){
            throw new IllegalArgumentException("Update data can't be null");
        }
    }
}
