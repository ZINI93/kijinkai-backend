package com.kijinkai.domain.order.entity;

import com.kijinkai.domain.BaseEntity;
import com.kijinkai.domain.customer.entity.Customer;
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

    @Column(name = "total_price_original", updatable = false)
    private BigDecimal totalPriceOriginal;   // 엔화의 상품전체가격

    @Column(name = "total_price_converted", updatable = false)
    private BigDecimal totalPriceConverted;   // 해당 통화의 전체가격

    @Column(name = "final_price_original", updatable = false)
    private BigDecimal finalPriceOriginal;   // 엔화의 배송비 포함된 전체 금액

    @Column(name = "converted_currency", updatable = false)
    private String convertedCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(name = "reject_reason")
    private String rejectedReason;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;


    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Builder
    public Order(String orderUuid, Customer customer, BigDecimal totalPriceOriginal, BigDecimal totalPriceConverted, BigDecimal finalPriceOriginal, String convertedCurrency, OrderStatus orderStatus, String memo, String rejectedReason, PaymentStatus paymentStatus) {
        this.orderUuid = orderUuid != null ? orderUuid:UUID.randomUUID().toString();
        this.customer = customer;
        this.totalPriceOriginal = totalPriceOriginal;
        this.totalPriceConverted = totalPriceConverted;
        this.finalPriceOriginal = finalPriceOriginal;
        this.convertedCurrency = convertedCurrency;
        this.orderStatus = orderStatus != null ? orderStatus: OrderStatus.DRAFT;
        this.memo = memo;
        this.rejectedReason = rejectedReason;
        this.paymentStatus = paymentStatus != null ? paymentStatus:PaymentStatus.PENDING;
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
}
