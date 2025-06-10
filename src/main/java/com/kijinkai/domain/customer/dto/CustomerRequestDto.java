package com.kijinkai.domain.customer.dto;

import com.kijinkai.domain.customer.entity.CustomerTier;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomerRequestDto {

    private String firstName;
    private String lastName;
    private String phoneNumber;

    public CustomerRequestDto(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}
