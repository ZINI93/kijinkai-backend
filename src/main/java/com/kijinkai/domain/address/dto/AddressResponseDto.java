package com.kijinkai.domain.address.dto;

import com.kijinkai.domain.customer.entity.Customer;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AddressResponseDto {

    private String addressUuid;
    private String customerUuid;
    private String recipientName;
    private String recipientPhoneNumber;
    private String country;
    private String zipcode;
    private String state;
    private String city;
    private String street;

    @Builder
    public AddressResponseDto(String customerUuid, String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street, String addressUuid) {
        this.customerUuid = customerUuid;
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.country = country;
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
        this.addressUuid = addressUuid;
    }
}
