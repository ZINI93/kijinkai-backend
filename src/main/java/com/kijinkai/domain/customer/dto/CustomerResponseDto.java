package com.kijinkai.domain.customer.dto;

import com.kijinkai.domain.customer.entity.CustomerTier;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomerResponseDto {

    private String customerUuid;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private CustomerTier customerTier;
    private String userUuid;

    @Builder
    public CustomerResponseDto(String customerUuid, String firstName, String lastName, String phoneNumber, CustomerTier customerTier, String userUuid) {
        this.customerUuid = customerUuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.customerTier = customerTier;
        this.userUuid = userUuid;
    }
}
