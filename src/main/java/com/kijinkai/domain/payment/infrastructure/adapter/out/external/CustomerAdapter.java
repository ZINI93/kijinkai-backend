package com.kijinkai.domain.payment.infrastructure.adapter.out.external;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.payment.application.port.out.CustomerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CustomerAdapter implements CustomerPort {

    private final CustomerPersistencePort customerPersistencePort;

    @Override
    public Optional<Customer> findByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid);
    }

    @Override
    public Optional<Customer> findByCustomerUuid(UUID customerUuid) {
        return customerPersistencePort.findByCustomerUuid(customerUuid);
    }
}
