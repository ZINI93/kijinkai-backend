package com.kijinkai.domain.customer.service;

import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.dto.CustomerUpdateDto;

public interface CustomerService {

    CustomerResponseDto createCustomerWithValidate(String userUuid, CustomerRequestDto requestDto);
    CustomerResponseDto updateCustomerWithValidate(String userUuid, String customerUuid, CustomerUpdateDto updateDto);
    CustomerResponseDto getCustomerInfo(String userUuid, String customerUuid);

}
