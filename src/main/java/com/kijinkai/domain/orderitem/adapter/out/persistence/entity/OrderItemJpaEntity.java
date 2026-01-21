package com.kijinkai.domain.orderitem.adapter.out.persistence.entity;

import com.kijinkai.domain.common.BaseEntity;
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
public class OrderItemJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long orderItemId;

    @Column(name = "order_item_uuid", nullable = false, updatable = false, unique = true)
    private UUID orderItemUuid;

    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Column(name = "delivery_uuid")
    private UUID deliveryUuid;

    @Column(name = "shipment_uuid")
    private UUID shipmentUuid;

    @Column(name = "order_item_code", nullable = false, updatable = false)
    private String orderItemCode;

    @JoinColumn(name = "order_id", updatable = false)
    private UUID orderUuid;

    @Column(name = "product_payment_uuid")
    private UUID productPaymentUuid;

    @Column(name = "delivery_fee_payment_uuid")
    private UUID deliveryFeePaymentUuid;

    @Column(name = "product_link", nullable = false, length = 255)
    private String productLink;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price_original", nullable = false, precision = 19, scale = 4)
    private BigDecimal priceOriginal;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_original", nullable = false)
    private Currency currencyOriginal;

    @Column(name = "memo",columnDefinition = "TEXT")
    private String memo;

    @Column(name = "inspection_requested", nullable = false)
    private Boolean inspectionRequested;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_status", nullable = false, length = 20)
    private OrderItemStatus orderItemStatus;



}

