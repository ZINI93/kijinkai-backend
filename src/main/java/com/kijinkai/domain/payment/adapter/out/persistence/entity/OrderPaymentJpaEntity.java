package com.kijinkai.domain.payment.adapter.out.persistence.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentOrder;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_payments")
@Entity
public class OrderPaymentJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_payment_id", nullable = false, updatable = false)
    private Long orderPaymentId;

    @Column(name = "payment_uuid", nullable = false, updatable = false, unique = true)
    private UUID paymentUuid;

    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Column(name = "wallet_uuid", nullable = false, updatable = false)
    private UUID walletUuid;

    @Column(name = "user_coupon_uuid", updatable = false)
    private UUID userCouponUuid;


    @Column(name = "order_uuid", updatable = false)
    private UUID orderUuid;

    @Column(name = "order_payment_code", nullable = false, updatable = false)
    private String orderPaymentCode;

    @Column(name = "payment_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal paymentAmount;

    @Column(name = "discount_amount", updatable = false)
    private BigDecimal discountAmount;

    @Column(name = "final_payment_amount", updatable = false)
    private BigDecimal finalPaymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_order", nullable = false, length = 20)
    private PaymentOrder paymentOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_payment_status", nullable = false, length = 30)
    private OrderPaymentStatus orderPaymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 30)
    private PaymentType paymentType;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    // 결제 완료 정보
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "create_by_admin_uuid", updatable = false)
    private UUID createdByAdminUuid;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

}
