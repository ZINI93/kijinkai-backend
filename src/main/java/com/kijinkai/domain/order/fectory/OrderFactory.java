package com.kijinkai.domain.order.fectory;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import org.springframework.stereotype.Component;


@Component
public class OrderFactory {

    public Order createOrder(Customer customer, String memo) {
        return Order.builder()
                .customer(customer)
                .memo(memo)
                .build();
    }
}