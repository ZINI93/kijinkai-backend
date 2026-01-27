package com.kijinkai.domain.customer.domain.model;

import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;
import com.kijinkai.domain.payment.domain.enums.BankType;
import jakarta.persistence.Convert;
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


    private BankType bankType;
    private String accountHolder;
    private String accountNumber;
    private String pcc;



    // 추가.

    // pcc 추가
    public void updatePcc(String pcc){
        if (pcc == null){
            throw new IllegalArgumentException("Update date can't be null");
        }
        this.pcc = pcc;
    }

    /**
     * 고객 정보 업데이트
     * @param customerUpdateDto
     */
    public void updateCustomer(String firstName, String lastName, String phoneNumber, String pcc, BankType bankType, String accountHolder, String accountNumber) {

        this.firstName = (firstName != null) ? firstName : this.firstName;
        this.lastName = (lastName != null) ? lastName : this.lastName;
        this.phoneNumber = (phoneNumber != null) ? phoneNumber : this.phoneNumber;
        this.pcc = (pcc != null) ? pcc : this.pcc;
        this.bankType =(bankType != null) ? bankType : this.bankType;
        this.accountHolder = (accountHolder != null) ? accountHolder : this.accountHolder;
        this.accountNumber = (accountNumber != null) ? accountNumber : this.accountNumber;

    }


    /**
     * 고객 정보 업데이트 검증
     * @param customerUpdateDto
     */
    private void validateUpdateData(CustomerUpdateDto customerUpdateDto){
        if (customerUpdateDto == null){
            throw new IllegalArgumentException("Update data can't be null");
        }
    }
}



