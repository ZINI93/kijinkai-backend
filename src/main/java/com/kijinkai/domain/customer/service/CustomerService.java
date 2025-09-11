package com.kijinkai.domain.customer.service;

import com.kijinkai.domain.customer.dto.CustomerCreateResponse;
import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.entity.CustomerTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {

    CustomerCreateResponse createCustomerWithValidate(UUID userUuid, CustomerRequestDto requestDto);
    CustomerResponseDto updateCustomerWithValidate(UUID userUuid, CustomerUpdateDto updateDto);

    CustomerResponseDto getCustomerInfo(UUID userUuid);
    Page<CustomerResponseDto> getAllByCustomers(UUID userUuid, String firstName, String lastName, String phoneNumber, CustomerTier customerTier, Pageable pageable);

    Customer findByUserUuid(UUID userUuid);
    Customer findByCustomerUuid(UUID customerUuid);
    
}
