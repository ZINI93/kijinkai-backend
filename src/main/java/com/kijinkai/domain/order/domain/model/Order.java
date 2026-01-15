package com.kijinkai.domain.order.domain.model;


import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    private Long orderId;
    private UUID orderUuid;
    private UUID customerUuid;
    private BigDecimal totalPriceOriginal;   // 엔화의 상품전체가격
    private BigDecimal totalPriceConverted;   // 해당 통화의 전체가격  // 현재 필요 없으나, 일단 필드 자체만 유지
    private BigDecimal finalPriceOriginal;   // 엔화의 배송비 포함된 전체 금액
    private Currency convertedCurrency;
    private OrderStatus orderStatus;
    private String orderCode;
    private String memo;
    private String rejectedReason;
    private PaymentType paymentType;  // 필요없을거 같음


    public Order(UUID customerUuid, String memo) {
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
