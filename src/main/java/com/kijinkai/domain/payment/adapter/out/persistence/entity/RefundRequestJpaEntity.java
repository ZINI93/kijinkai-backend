package com.kijinkai.domain.payment.adapter.out.persistence.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.payment.domain.enums.RefundStatus;
import com.kijinkai.domain.payment.domain.enums.RefundType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refund_requests")
@Entity
public class RefundRequestJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_request_id", nullable = false, updatable = false)
    private Long refundRequestId;

    @Column(name = "refund_uuid", nullable = false, updatable = false, unique = true)
    private UUID refundUuid;

    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Column(name = "wallet_uuid", nullable = false, updatable = false)
    private UUID walletUuid;

    @Column(name = "order_item_uuid", nullable = false, updatable = false)
    private UUID orderItemUuid;

    // 환불 금액
    @Column(name = "refund_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal refundAmount;

    // 환불 사유
    @Column(name = "refund_reason", nullable = false, length = 255)
    private String refundReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_type", nullable = false, length = 30)
    private RefundType refundType; // STOCK_OUT, PURCHASE_CANCELLED, DEFECTIVE_PRODUCT 등

    // 상태 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RefundStatus status;

    // 처리 정보
    @Column(name = "processed_by_admin_uuid", nullable = false, updatable = false)
    private UUID processedByAdmin;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "admin_memo", columnDefinition = "TEXT")
    private String adminMemo;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

}
