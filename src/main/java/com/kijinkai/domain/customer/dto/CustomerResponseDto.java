package com.kijinkai.domain.customer.dto;

import com.kijinkai.domain.customer.entity.CustomerTier;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Getter
public class CustomerResponseDto {

    private UUID customerUuid;
    private UUID userUuid;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private CustomerTier customerTier;


    @Builder
    public CustomerResponseDto(UUID customerUuid, String firstName, String lastName, String phoneNumber, CustomerTier customerTier, UUID userUuid) {
        this.customerUuid = customerUuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.customerTier = customerTier;
        this.userUuid = userUuid;
    }

    @QueryProjection
    public CustomerResponseDto(String firstName, String lastName, String phoneNumber, CustomerTier customerTier) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.customerTier = customerTier;
    }
}
