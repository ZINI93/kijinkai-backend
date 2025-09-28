package com.kijinkai.domain.customer.application.event;


import com.kijinkai.domain.customer.domain.event.CustomerCreateEvent;
import com.kijinkai.domain.customer.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class CustomerEventPublisher {

    private final ApplicationEventPublisher eventPublisher;


    public void publishCustomerCreatedEvent(Customer customer){
        String correlationId = UUID.randomUUID().toString();
        CustomerCreateEvent event = new CustomerCreateEvent(customer, LocalDateTime.now(), correlationId);

        log.info("Publishing customer created event: customerUuid={}, correlationId={}",
                customer.getCustomerUuid(), correlationId);

        eventPublisher.publishEvent(event);
    }
}
