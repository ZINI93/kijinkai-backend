package com.kijinkai.domain.address.entity;

import com.kijinkai.domain.address.dto.AddressUpdateDto;
import com.kijinkai.domain.common.TimeBaseEntity;
import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.domain.model.Customer;
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
public class Address extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long addressId;

    @Column(name = "address_uuid", nullable = false, updatable = false, unique = true)
    private UUID addressUuid;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
//    private Customer customer;

    @Column(name = "customer_uuid")
    private UUID customerUuid;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_phone_number", nullable = false)
    private String recipientPhoneNumber;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "zipcode", nullable = false)
    private String zipcode;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;


    @Builder
    public Address(UUID addressUuid, UUID customerUuid, String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street, Boolean isDefault) {
        this.addressUuid = addressUuid != null ? addressUuid : UUID.randomUUID();
        this.customerUuid = customerUuid;
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.country = country;
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
        this.isDefault = true;
    }


    public void updateAddress(AddressUpdateDto addressUpdateDto) {
        this.recipientName = addressUpdateDto.getRecipientName();
        this.recipientPhoneNumber = addressUpdateDto.getRecipientPhoneNumber();
        this.country = addressUpdateDto.getCountry();
        this.zipcode = addressUpdateDto.getZipcode();
        this.state = addressUpdateDto.getState();
        this.city = addressUpdateDto.getCity();
        this.street = addressUpdateDto.getStreet();
    }
}
