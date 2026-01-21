package com.kijinkai.domain.payment.domain.model;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentOrder;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderPayment {

    private Long orderPaymentId;
    private UUID paymentUuid;
    private String orderPaymentCode;

    private UUID customerUuid;
    private UUID walletUuid;
    private UUID orderUuid;
    private BigDecimal paymentAmount;
    private PaymentOrder paymentOrder;
    private OrderPaymentStatus orderPaymentStatus;
    private PaymentType paymentType;
    private String rejectReason;
    private LocalDateTime paidAt;
    private UUID createdByAdminUuid;
    private Long version;

    private LocalDateTime createdAt;

    // 도메인 로직: 결제 완료
    public void complete() {
        validateCanComplete();
        this.orderPaymentStatus = OrderPaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }

    // 도메인 로직: 결제 완료
    public void OrderSecondComplete(OrderJpaEntity order) {
        if (order.getOrderStatus() != OrderStatus.FIRST_PAID) {
            throw new IllegalStateException("첫번째 지불을 하지 않았습니다.");
        }

        validateCanComplete();
        this.orderPaymentStatus = OrderPaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }

    // 도메인 로직: 결제 실패
    public void fail(String reason) {
        if (this.orderPaymentStatus != OrderPaymentStatus.PENDING) {
            throw new IllegalStateException("대기 중인 상태가 아닙니다");
        }

        this.orderPaymentStatus = OrderPaymentStatus.FAILED;

    }

    private void validateCanComplete() {
        if (this.orderPaymentStatus != OrderPaymentStatus.PENDING) {
            throw new IllegalStateException("완료 가능한 상태가 아닙니다: " + this.orderPaymentStatus);
        }
        if (this.paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("결제 금액이 올바르지 않습니다");
        }
    }

    public boolean isPending() {
        return this.orderPaymentStatus == OrderPaymentStatus.PENDING;
    }

    public boolean isCompleted() {
        return this.orderPaymentStatus == OrderPaymentStatus.COMPLETED;
    }

    public void markAsFailed(String reason) {
        if (this.orderPaymentStatus == OrderPaymentStatus.COMPLETED) {
            throw new IllegalStateException("Completed request cannot fail");
        }
        this.orderPaymentStatus = OrderPaymentStatus.FAILED;
        this.rejectReason = reason;
    }


    public void updateTotalAmount(BigDecimal amount) {
        this.paymentAmount = amount;
    }
}
