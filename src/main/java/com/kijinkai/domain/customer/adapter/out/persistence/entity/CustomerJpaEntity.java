package com.kijinkai.domain.customer.adapter.out.persistence.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.customer.domain.model.CustomerTier;
import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "customers")
@Entity
public class CustomerJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id", nullable = false, updatable = false, unique = true)
    private Long customerId;

    @Column(name = "customer_uuid", nullable = false, updatable = false, unique = true)
    private UUID customerUuid;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_tier", nullable = false)
    private CustomerTier customerTier;

    @Column(name = "user_uuid", nullable = false, unique = true)
    private UUID userUuid;

}


