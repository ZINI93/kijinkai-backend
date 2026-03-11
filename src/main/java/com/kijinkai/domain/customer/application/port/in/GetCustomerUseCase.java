package com.kijinkai.domain.customer.application.port.in;

import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.domain.model.CustomerTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetCustomerUseCase {

    CustomerResponseDto getCustomerInfo(UUID userUuid);
    Page<CustomerResponseDto> getCustomers(UUID userUuid, String email, String name, String phoneNumber, CustomerTier customerTier, Pageable pageable);
}
