package com.kijinkai.domain.customer.repository;

import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.entity.CustomerTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerRepositoryCustom {

    Page<CustomerResponseDto> findAllByCustomers(UUID userUuid, String firstName, String lastName, String phoneNumber, CustomerTier customerTier, Pageable pageable);
}
