package com.kijinkai.domain.customer.domain.model;

import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;
import lombok.*;

import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    private Long customerId;
    private UUID customerUuid;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private CustomerTier customerTier;
    private UUID userUuid;

    /**
     * 고객 정보 업데이트
     * @param customerUpdateDto
     */


    public void updateCustomer(CustomerUpdateDto customerUpdateDto) {
        this.firstName = customerUpdateDto.getFirstName();
        this.lastName = customerUpdateDto.getLastName();
        this.phoneNumber = customerUpdateDto.getPhoneNumber();
    }

}



