package com.kijinkai.domain.delivery.entity;


import com.kijinkai.domain.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Table(name = "deliveries")
@Entity
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id", nullable = false, updatable = false, unique = true)
    private Long deliveryId;

    @Column(name = "delivery_uuid", nullable = false, updatable = false, unique = true)
    private String deliveryUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "post_code", nullable = false)
    private String postalCode;

    @Column(name = "address1", nullable = false)
    private String address1;

    @Column(name = "address2", nullable = false)
    private String address2;

    @Column(name = "memo")
    private String memo;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;


    @Builder
    public Delivery(String deliveryUuid, Customer customer, String receiverName, String postalCode, String address1, String address2, String memo, Boolean isDefault) {
        this.deliveryUuid = UUID.randomUUID().toString();
        this.customer = customer;
        this.receiverName = receiverName;
        this.postalCode = postalCode;
        this.address1 = address1;
        this.address2 = address2;
        this.memo = memo;
        this.isDefault = Boolean.TRUE;
    }

    public void updateDelivery(String receiverName, String postalCode, String address1, String address2, String memo) {
        this.receiverName = receiverName;
        this.postalCode = postalCode;
        this.address1 = address1;
        this.address2 = address2;
        this.memo = memo;
    }
}
