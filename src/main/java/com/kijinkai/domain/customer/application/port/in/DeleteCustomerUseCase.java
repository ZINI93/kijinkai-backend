package com.kijinkai.domain.customer.application.port.in;

import com.kijinkai.domain.customer.domain.model.Customer;

import java.util.UUID;

public interface DeleteCustomerUseCase {

    void deleteCustomer(Customer customer);
}
