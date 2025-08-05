package com.kijinkai.domain.payment.domain.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.payment.domain.enums.RefundStatus;
import com.kijinkai.domain.payment.domain.enums.RefundType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refund_requests")
@Entity
public class RefundRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_request_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "refund_uuid", nullable = false, updatable = false, unique = true)
    private UUID refundUuid;

    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Column(name = "wallet_uuid", nullable = false, updatable = false)
    private UUID walletUuid;

    @Column(name = "order_item_uuid", nullable = false, updatable = false)
    private UUID orderItemUuid;

    // 환불 금액
    @Column(name = "refund_amount", nullable = false)
    private BigDecimal refundAmount;

    // 환불 사유
    @Column(name = "refund_reason", nullable = false)
    private String refundReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_type", nullable = false)
    private RefundType refundType; // STOCK_OUT, PURCHASE_CANCELLED, DEFECTIVE_PRODUCT 등

    // 상태 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RefundStatus status;

    // 처리 정보
    @Column(name = "processed_by_admin", nullable = false, updatable = false)
    private UUID processedByAdmin;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "admin_memo")
    private String adminMemo;

    @Version
    private Long version;

    @Builder
    public RefundRequest(UUID customerUuid, UUID walletUuid, UUID orderItemUuid,
                         BigDecimal refundAmount, String refundReason, RefundType refundType,
                         UUID processedByAdmin) {
        this.refundUuid = UUID.randomUUID();
        this.customerUuid = customerUuid;
        this.walletUuid = walletUuid;
        this.orderItemUuid = orderItemUuid;
        this.refundAmount = refundAmount;
        this.refundReason = refundReason;
        this.refundType = refundType;
        this.processedByAdmin = processedByAdmin;
        this.status = RefundStatus.PROCESSING;
    }
    // 도메인 로직: 환불 완료
    public void complete(String adminMemo) {
        validateCanComplete();

        this.status = RefundStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
        this.adminMemo = adminMemo;

    }


    // 도메인 로직: 환불 실패
    public void fail(String reason) {
        if (this.status != RefundStatus.PROCESSING) {
            throw new IllegalStateException("처리 중인 상태가 아닙니다");
        }

        this.status = RefundStatus.FAILED;
        this.processedAt = LocalDateTime.now();
        this.adminMemo = "환불 실패: " + reason;
    }

    private void validateCanComplete() {
        if (this.status != RefundStatus.PROCESSING) {
            throw new IllegalStateException("완료 가능한 상태가 아닙니다: " + this.status);
        }
        if (this.refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("환불 금액이 올바르지 않습니다");
        }
    }

    public boolean isProcessing() {
        return this.status == RefundStatus.PROCESSING;
    }
}
