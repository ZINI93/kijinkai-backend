package com.kijinkai.domain.customer.domain.factory;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.customer.domain.model.CustomerTier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerFactory {

    public Customer createCustomer(UUID userUuid, String firstName, String lastName, String phoneNumber){

        validateCreateInput(userUuid, firstName, lastName, phoneNumber);

        return Customer.builder()
                .customerUuid(UUID.randomUUID())
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .customerTier(CustomerTier.BRONZE)
                .userUuid(userUuid)
                .build();
    }

    private void validateCreateInput(UUID UserUuid, String firstName, String lastName, String phoneNumber){
        if (UserUuid == null){
            throw new IllegalArgumentException("User uuid can't be null");
        }
        if  (firstName == null && lastName == null && phoneNumber == null){
            throw new IllegalArgumentException("Customer request can't be null");
        }

    }
}
