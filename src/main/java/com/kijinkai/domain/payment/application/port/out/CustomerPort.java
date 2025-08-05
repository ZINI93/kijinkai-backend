package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.customer.entity.Customer;

import java.util.UUID;

public interface CustomerPort {
    Customer findByUserUuid(UUID userUuid);
    Customer findByCustomerUuid(UUID customerUuid);
}
