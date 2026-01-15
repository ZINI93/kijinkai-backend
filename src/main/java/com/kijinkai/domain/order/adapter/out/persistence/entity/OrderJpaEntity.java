package com.kijinkai.domain.order.adapter.out.persistence.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.PaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "orders")
@Entity
public class OrderJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long orderId;

    @Column(name = "order_uuid", nullable = false, updatable = false , unique = true)
    private UUID orderUuid;

    @Column(name = "order_code", nullable = false, updatable = false, unique = true)
    private String orderCode;

    @Column(name = "customer_uuid", nullable = false)
    private UUID customerUuid;

    @Column(name = "total_price_original", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalPriceOriginal;   // 엔화의 상품전체가격

    @Column(name = "final_price_original", precision = 16, scale = 4)
    private BigDecimal finalPriceOriginal;   // 엔화의 배송비 포함된 전체 금액

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "rejected_reason", length = 255)
    private String rejectedReason;

}
