package com.kijinkai.domain.payment.domain.model;

import com.kijinkai.domain.payment.domain.enums.RefundStatus;
import com.kijinkai.domain.payment.domain.enums.RefundType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundRequest {

    private Long refundRequestId;

    private UUID refundUuid;

    private UUID customerUuid;

    private UUID walletUuid;

    private UUID orderItemUuid;

    private BigDecimal refundAmount;

    private String refundReason;

    private RefundType refundType;

    private RefundStatus status;

    private UUID processedByAdmin;

    private LocalDateTime processedAt;

    private String adminMemo;

    private Long version;

    private LocalDateTime createdAt;
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
