package com.kijinkai.domain.customer.adapter.out.persistence.repository;

import com.kijinkai.domain.customer.domain.model.CustomerTier;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Builder
@Data
public class CustomerSearchCondition {

    private String email;
    private String name;
    private String phoneNumber;
    private CustomerTier customerTier;
}
