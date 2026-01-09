package com.kijinkai.domain.customer.application.port.in;

import com.kijinkai.domain.customer.application.dto.CustomerCreateResponse;
import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.domain.model.Customer;

import java.util.UUID;

public interface CreateCustomerUseCase {


    Customer createCustomer(UUID userUuid, String firstName, String lastName, String phoneNumber);

}
