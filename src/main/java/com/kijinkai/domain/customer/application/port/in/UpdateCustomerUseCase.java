package com.kijinkai.domain.customer.application.port.in;

import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;

import java.util.UUID;

public interface UpdateCustomerUseCase {

    CustomerResponseDto updateCustomer(UUID userUuid, CustomerUpdateDto updateDto);
}
