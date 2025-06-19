package com.kijinkai.domain.address.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressUpdateDto {


    private String recipientName;
    private String recipientPhoneNumber;
    private String country;
    private String zipcode;
    private String state;
    private String city;
    private String street;

    @Builder
    public AddressUpdateDto(String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street) {
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.country = country;
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
    }
}
