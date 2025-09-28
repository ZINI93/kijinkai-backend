package com.kijinkai.domain.customer.application.port.out.persistence;


import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.customer.domain.model.CustomerTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomerPersistencePort {

    Customer saveCustomer(Customer customer);

    Optional<Customer> findByUserUuidAndCustomerUuid(UUID userUuid, UUID customerUuid);
    Optional<Customer> findByUserUuid(UUID userUuid);
    Optional<Customer> findByCustomerUuid(UUID customerUuid);

    Page<Customer> findAllByCustomers(String firstName, String lastName, String phoneNumber, CustomerTier customerTier, Pageable pageable);

    void deleteCustomer(Customer customer);
}
