package com.kijinkai.domain.address.adapter.out.persistence.entity;

import com.kijinkai.domain.common.TimeBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "addresses")
@Entity
public class AddressJpaEntity extends TimeBaseEntity {

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


}
