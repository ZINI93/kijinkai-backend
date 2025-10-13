package com.kijinkai.domain.orderitem.adapter.out.persistence.entity;

import com.kijinkai.domain.common.TimeBaseEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_items")
@Entity
public class OrderItemJpaEntity extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long orderItemId;

    @Column(name = "order_item_uuid", nullable = false, updatable = false, unique = true)
    private UUID orderItemUuid;

    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, nullable = false)
    private OrderJpaEntity order;

    @Column(name = "product_payment_uuid")
    private UUID productPaymentUuid;

    @Column(name = "delivery_fee_payment_uuid")
    private UUID deliveryFeePaymentUuid;

    @Column(name = "product_link", nullable = false)
    private String productLink;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price_original", nullable = false)
    private BigDecimal priceOriginal;

    @Column(name = "price_converted", nullable = false)
    private BigDecimal priceConverted;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_currency_original", nullable = false)
    private Currency currencyOriginal; //JYP

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_currency_converted", nullable = false)
    private Currency currencyConverted;

    @Column(name = "exchange_rate", nullable = false, updatable = false)
    private BigDecimal exchangeRate;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_status", nullable = false)
    private OrderItemStatus orderItemStatus;

}

