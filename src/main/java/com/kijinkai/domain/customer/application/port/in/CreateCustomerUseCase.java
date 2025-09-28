package com.kijinkai.domain.customer.application.port.in;

import com.kijinkai.domain.customer.application.dto.CustomerCreateResponse;
import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;

import java.util.UUID;

public interface CreateCustomerUseCase {


    CustomerCreateResponse createCustomer(UUID userUuid, CustomerRequestDto requestDto);

}
