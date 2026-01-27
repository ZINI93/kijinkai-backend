package com.kijinkai.domain.customer.application.port.in;

import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.payment.domain.enums.BankType;

import java.util.UUID;

public interface UpdateCustomerUseCase {

    UUID updateCustomer(UUID userUuid, String firstName, String lastName, String phoneNumber, String pcc, BankType bankType, String accountHolder, String accountNumber);
    void updatePcc(Customer customer, String pcc);
}
