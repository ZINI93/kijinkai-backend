package com.kijinkai.domain.customer.domain.event;

import com.kijinkai.domain.customer.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class CustomerCreateEvent {

    private final Customer customer;
    private final LocalDateTime createdAt;
    private final String correlationId;
}
