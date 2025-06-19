package com.kijinkai.domain.address.entity;

import com.kijinkai.domain.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "addresses")
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long addressId;

    @Column(name = "address_uuid", nullable = false, updatable = false, unique = true)
    private String addressUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;

    private String recipientName;

    private String recipientPhoneNumber;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;


    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;


    @Builder
    public Address (String addressUuid, Customer customer, String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street, Boolean isDefault) {
        this.addressUuid = addressUuid != null ? addressUuid : UUID.randomUUID().toString();
        this.customer = customer;
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.country = country;
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
        this.isDefault = true;
    }


    public void updateAddress(String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street) {
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.country = country;
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
    }
}
