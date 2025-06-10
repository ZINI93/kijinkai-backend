package com.kijinkai.domain.order.dto;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.OrderStatus;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class OrderResponseDto {

    private String orderUuid;
    private Customer customer;
    private BigDecimal deliveryFee;
    private BigDecimal totalPriceOriginal;   // 엔화의 상품전체가격
    private BigDecimal totalPriceConverted;   // 해당 통화의 전체가격
    private BigDecimal finalPriceOriginal;   // 엔화의 배송비 포함된 전체 금액
    private String convertedCurrency;
    private OrderStatus orderstate;
    private String memo;
    private String rejectedReason;
    private PaymentStatus paymentStatus;


    @Builder
    public OrderResponseDto(String orderUuid, Customer customer, BigDecimal deliveryFee, BigDecimal totalPriceOriginal, BigDecimal totalPriceConverted, BigDecimal finalPriceOriginal, String convertedCurrency, OrderStatus orderstate, String memo, String rejectedReason, PaymentStatus paymentStatus) {
        this.orderUuid = orderUuid;
        this.customer = customer;
        this.deliveryFee = deliveryFee;
        this.totalPriceOriginal = totalPriceOriginal;
        this.totalPriceConverted = totalPriceConverted;
        this.finalPriceOriginal = finalPriceOriginal;
        this.convertedCurrency = convertedCurrency;
        this.orderstate = orderstate;
        this.memo = memo;
        this.rejectedReason = rejectedReason;
        this.paymentStatus = paymentStatus;
    }
}
