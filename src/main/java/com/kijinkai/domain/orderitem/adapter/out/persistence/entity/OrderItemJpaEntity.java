package com.kijinkai.domain.orderitem.adapter.out.persistence.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.common.TimeBaseEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @JoinColumn(name = "order_uuid", updatable = false)
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

//    @Column(name = "inspection_requested", nullable = false)
//    private Boolean inspectionRequested;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_status", nullable = false)
    private InspectionStatus inspectionStatus;

    @Column(name = "inspected_at")
    private LocalDateTime inspectedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_status", nullable = false, length = 20)
    private OrderItemStatus orderItemStatus;


    @Column(name = "reject_reason")
    private String rejectReason;

    // 구매 가격 스냅샷
    @Column(name = "applied_rate")
    private BigDecimal appliedRate;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "paid_amount" )
    private BigDecimal paidAmount;

    @Column(name = "local_arrived_at")
    private LocalDateTime localArrivedAt;

}

