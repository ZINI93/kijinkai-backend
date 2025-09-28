package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerPort {
    Optional<Customer> findByUserUuid(UUID userUuid);
    Optional<Customer> findByCustomerUuid(UUID customerUuid);
}
