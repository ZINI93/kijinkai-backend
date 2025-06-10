package com.kijinkai.domain.payment.entity;


import com.kijinkai.domain.BaseEntity;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.orderitem.entity.Currency;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
@Entity
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long paymentId;

    private String paymentUuid;
    private Customer customer;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private Currency currency;
    private PaymentType paymentType;
    private String description;
    private String externalTransactionId;

}
