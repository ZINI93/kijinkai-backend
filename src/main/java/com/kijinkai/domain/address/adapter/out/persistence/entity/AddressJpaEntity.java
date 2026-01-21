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
    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @Column(name = "address_uuid", nullable = false, updatable = false, unique = true)
    private UUID addressUuid;

    @Column(name = "customer_uuid", unique = true, nullable = false)
    private UUID customerUuid;

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(name = "recipient_phone_number", nullable = false, length = 20)
    private String recipientPhoneNumber;

    @Column(name = "zipcode", nullable = false, length = 20)
    private String zipcode;

    @Column(name = "street_address", nullable = false, length = 100)
    private String streetAddress;

    @Column(name = "detail_address", nullable = false, length = 255)
    private String detailAddress;

    @Column(name = "pccc")
    private String pccc;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;
}
