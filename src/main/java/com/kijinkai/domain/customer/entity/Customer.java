package com.kijinkai.domain.customer.entity;

import com.kijinkai.domain.BaseEntity;
import com.kijinkai.domain.customer.dto.CustomerUpdateDto;
import com.kijinkai.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Getter
@Table(name = "customers")
@Entity
public class Customer extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, unique = true)
    private User user;


    @Builder
    public Customer(UUID customerUuid, String firstName, String lastName, String phoneNumber, CustomerTier customerTier, User user) {
        this.customerUuid = customerUuid != null ? customerUuid : UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.customerTier = customerTier != null ? customerTier : CustomerTier.BRONZE;
        this.user = user;
    }

    public void updateCustomer(CustomerUpdateDto customerUpdateDto) {
        this.firstName = customerUpdateDto.getFirstName();
        this.lastName = customerUpdateDto.getLastName();
        this.phoneNumber = customerUpdateDto.getLastName();
    }
//
//    public void updateCustomerTier(){
//        this.customerTier = ;
//    }
}


