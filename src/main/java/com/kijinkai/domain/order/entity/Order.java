package com.kijinkai.domain.order.entity;

import com.kijinkai.domain.BaseEntity;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.orderitem.entity.Currency;
import com.kijinkai.domain.payment.entity.Payment;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import com.kijinkai.domain.payment.entity.PaymentType;
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
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long orderId;

    @Column(name = "order_uuid", nullable = false, updatable = false , unique = true)
    private String orderUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "total_price_original")
    private BigDecimal totalPriceOriginal;   // 엔화의 상품전체가격

    @Column(name = "total_price_converted")
    private BigDecimal totalPriceConverted;   // 해당 통화의 전체가격

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

    private PaymentType paymentType;


    @Builder
    public Order(String orderUuid, Customer customer, BigDecimal totalPriceOriginal, BigDecimal totalPriceConverted, BigDecimal finalPriceOriginal, Currency convertedCurrency, OrderStatus orderStatus, String memo, String rejectedReason, PaymentStatus paymentStatus, PaymentType paymentType) {
        this.orderUuid = orderUuid != null ? orderUuid:UUID.randomUUID().toString();
        this.customer = customer;
        this.totalPriceOriginal = totalPriceOriginal;
        this.totalPriceConverted = totalPriceConverted;
        this.finalPriceOriginal = finalPriceOriginal;
        this.convertedCurrency = convertedCurrency;
        this.orderStatus = orderStatus != null ? orderStatus: OrderStatus.DRAFT;
        this.memo = memo;
        this.rejectedReason = rejectedReason;
        this.paymentType = paymentType != null ? paymentType : PaymentType.CREDIT;
    }

    public Order(Customer customer, String memo) {
        this.customer = customer;
        this.memo = memo;
    }

    public void updateOrderState(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
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
        this.orderStatus = OrderStatus.PAID;
    }

    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;
    }

    public void pendingApprovalOrder() {
        this.orderStatus = OrderStatus.PENDING_APPROVAL;
    }
}
