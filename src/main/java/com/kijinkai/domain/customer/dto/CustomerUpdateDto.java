package com.kijinkai.domain.customer.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomerUpdateDto {

    private String firstName;
    private String lastName;
    private String phoneNumber;

    public CustomerUpdateDto(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}
