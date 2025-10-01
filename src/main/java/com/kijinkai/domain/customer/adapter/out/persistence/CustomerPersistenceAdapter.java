package com.kijinkai.domain.customer.adapter.out.persistence;


import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.adapter.out.persistence.mapper.CustomerPersistenceMapper;
import com.kijinkai.domain.customer.adapter.out.persistence.repository.CustomerRepository;
import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.customer.domain.model.CustomerTier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerPersistencePort {

    private final CustomerRepository customerRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;


    @Override
    public Customer saveCustomer(Customer customer) {
        CustomerJpaEntity customerJpaEntity = customerPersistenceMapper.toCustomerJpaEntity(customer);
        customerJpaEntity = customerRepository.save(customerJpaEntity);
        return customerPersistenceMapper.toCustomer(customerJpaEntity);
    }

    @Override
    public Optional<Customer> findByUserUuidAndCustomerUuid(UUID userUuid, UUID customerUuid) {
        return customerRepository.findByUserUuidAndCustomerUuid(userUuid,customerUuid)
                .map(customerPersistenceMapper::toCustomer);
    }

    @Override
    public Optional<Customer> findByUserUuid(UUID userUuid) {
         return customerRepository.findByUserUuid(userUuid)
                .map(customerPersistenceMapper::toCustomer);
    }

    @Override
    public Optional<Customer> findByCustomerUuid(UUID customerUuid) {
        return customerRepository.findByCustomerUuid(customerUuid)
                .map(customerPersistenceMapper::toCustomer);
    }

    @Override
    public Page<Customer> findAllByCustomers(String firstName, String lastName, String phoneNumber, CustomerTier customerTier, Pageable pageable) {
        return customerRepository.findAllByCustomers(firstName, lastName, phoneNumber, customerTier, pageable)
                .map(customerPersistenceMapper::toCustomer);
    }

    @Override
    public void deleteCustomer(Customer customer) {
        CustomerJpaEntity customerJpaEntity = customerPersistenceMapper.toCustomerJpaEntity(customer);
        customerRepository.delete(customerJpaEntity);
    }
}
