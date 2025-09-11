package com.kijinkai.domain.payment.infrastructure.adapter.out.external;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.service.CustomerService;
import com.kijinkai.domain.payment.application.port.out.CustomerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CustomerAdapter implements CustomerPort {

    private final CustomerService customerService;

    @Override
    public Customer findByUserUuid(UUID userUuid) {
        return customerService.findByUserUuid(userUuid);
    }

    @Override
    public Customer findByCustomerUuid(UUID customerUuid) {
        return customerService.findByCustomerUuid(customerUuid);
    }
}
