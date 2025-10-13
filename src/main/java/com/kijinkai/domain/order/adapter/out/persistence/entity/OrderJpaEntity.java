package com.kijinkai.domain.order.adapter.out.persistence.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.PaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Entity
public class OrderJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long orderId;

    @Column(name = "order_uuid", nullable = false, updatable = false , unique = true)
    private UUID orderUuid;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_id", nullable = false)
//    private Customer customer;


    @Column(name = "customer_uuid")
    private UUID customerUuid;

    @Column(name = "total_price_original", nullable = false)
    private BigDecimal totalPriceOriginal;   // 엔화의 상품전체가격

    @Column(name = "total_price_converted")
    private BigDecimal totalPriceConverted;   // 해당 통화의 전체가격  // 현재 필요 없으나, 일단 필드 자체만 유지

    @Column(name = "final_price_original")
    private BigDecimal finalPriceOriginal;   // 엔화의 배송비 포함된 전체 금액

    @Enumerated(EnumType.STRING)
    @Column(name = "converted_currency")
    private Currency convertedCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(name = "reject_reason")
    private String rejectedReason;

    private PaymentType paymentType;  // 필요없을거 같음

    @Builder
    public OrderJpaEntity(UUID orderUuid, UUID customerUuid, BigDecimal totalPriceOriginal, BigDecimal totalPriceConverted, BigDecimal finalPriceOriginal, Currency convertedCurrency, OrderStatus orderStatus, String memo, String rejectedReason, PaymentStatus paymentStatus, PaymentType paymentType) {
        this.orderUuid = orderUuid != null ? orderUuid:UUID.randomUUID();
        this.customerUuid = customerUuid;
        this.totalPriceOriginal = totalPriceOriginal;
        this.totalPriceConverted = totalPriceConverted;
        this.finalPriceOriginal = finalPriceOriginal;
        this.convertedCurrency = convertedCurrency;
        this.orderStatus = orderStatus;
        this.memo = memo;
        this.rejectedReason = rejectedReason;
        this.paymentType = paymentType;
    }

    public OrderJpaEntity(UUID customerUuid, String memo) {
        this.customerUuid = customerUuid;
        this.memo = memo;
    }

    public void fistOrderPayment(){
        this.orderStatus = OrderStatus.FIRST_PAID;
    }
    public void secondOrderPayment(){
        this.orderStatus = OrderStatus.SECOND_PAID;
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void prepareDeliveryOrder(){
        this.orderStatus = OrderStatus.PREPARE_DELIVERY;
    }

    public void updateOrder(OrderStatus orderStatus, String memo) {
        this.orderStatus = orderStatus;
        this.memo = memo;
    }

    public void updateOrderEstimate(BigDecimal totalPriceOriginal, BigDecimal totalPriceConverted, Currency convertedCurrency, String memo) {
        this.totalPriceOriginal = totalPriceOriginal;
        this.totalPriceConverted = totalPriceConverted;
        this.convertedCurrency = convertedCurrency;
        this.orderStatus = OrderStatus.AWAITING_PAYMENT;
        this.memo = memo;
    }

    public void completePayment() {
        this.orderStatus = OrderStatus.FIRST_PAID;
    }

    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;
    }

    public void pendingApprovalOrder() {
        this.orderStatus = OrderStatus.PENDING_APPROVAL;
    }
}
