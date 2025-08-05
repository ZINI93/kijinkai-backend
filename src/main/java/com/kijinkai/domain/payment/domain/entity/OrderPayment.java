package com.kijinkai.domain.payment.domain.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.entity.OrderStatus;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
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
@Table(name = "order_payments")
@Entity
public class OrderPayment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_payment_id", nullable = false, updatable = false )
    private Long id;

    @Column(name = "payment_uuid", nullable = false, updatable = false, unique = true)
    private UUID paymentUuid;

    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Column(name = "wallet_uuid", nullable = false, updatable = false)
    private UUID walletUuid;

    @Column(name = "order_uuid", nullable = false, updatable = false)
    private UUID orderUuid;

    @Column(name = "payment_amount", nullable = false)
    private BigDecimal paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderPaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "reject_reason")
    private String rejectReason;

    // 결제 완료 정보
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "create_by_adminUuid", nullable = false, updatable = false)
    private UUID createdByAdminUuid;

    @Version
    private Long version;



    @Builder
    public OrderPayment(UUID customerUuid, UUID walletUuid, UUID orderUuid, BigDecimal paymentAmount, PaymentType paymentType, UUID createdByAdminUuid) {
        this.paymentUuid = UUID.randomUUID();
        this.customerUuid = customerUuid;
        this.walletUuid = walletUuid;
        this.paymentType = paymentType;
        this.orderUuid = orderUuid;
        this.paymentAmount = paymentAmount;
        this.status = OrderPaymentStatus.PENDING;
        this.createdByAdminUuid = createdByAdminUuid;
    }

    // 도메인 로직: 결제 완료
    public void complete() {
        validateCanComplete();
        this.status = OrderPaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }

    // 도메인 로직: 결제 완료
    public void OrderSecondComplete(Order order) {
        if (order.getOrderStatus() != OrderStatus.FIRST_PAID){
            throw new IllegalStateException("첫번째 지불을 하지 않았습니다.");
        }

        validateCanComplete();
        this.status = OrderPaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }

    // 도메인 로직: 결제 실패
    public void fail(String reason) {
        if (this.status != OrderPaymentStatus.PENDING) {
            throw new IllegalStateException("대기 중인 상태가 아닙니다");
        }

        this.status = OrderPaymentStatus.FAILED;

    }

    private void validateCanComplete() {
        if (this.status != OrderPaymentStatus.PENDING) {
            throw new IllegalStateException("완료 가능한 상태가 아닙니다: " + this.status);
        }
        if (this.paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("결제 금액이 올바르지 않습니다");
        }
    }

    public boolean isPending() {
        return this.status == OrderPaymentStatus.PENDING;
    }

    public boolean isCompleted() {
        return this.status == OrderPaymentStatus.COMPLETED;
    }

    public void markAsFailed(String reason){
        if (this.status == OrderPaymentStatus.COMPLETED){
            throw new IllegalStateException("완료된 요청은 실패 처리 될 수 없습니다.");
        }
        this.status = OrderPaymentStatus.FAILED;
        this.rejectReason = reason;
    }
}
