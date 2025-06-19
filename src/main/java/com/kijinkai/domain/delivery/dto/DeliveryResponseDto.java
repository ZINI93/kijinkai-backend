package com.kijinkai.domain.delivery.dto;

import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class DeliveryResponseDto {

    private String deliveryUuid;
    private String orderUuid;
    private String customerUuid;
    private DeliveryStatus deliveryStatus;

    // --- 배송 주소 스냅샷 (Snapshot) ---
    private String recipientName;
    private String recipientPhoneNumber;
    private String country;
    private String zipcode;
    private String state;
    private String city;
    private String street;
    // ------------------------------------

    private String carrier;
    private String trackingNumber;
    private BigDecimal deliveryFee;
    private LocalDateTime estimatedDeliveryAt; // 예상 배송 완료 일시
    private LocalDateTime shippedAt; // 실제 발송 일시
    private LocalDateTime deliveredAt; // 실제 배송 완료 일시
    private String deliveryRequest; // 배송 요청 사항
    private String cancelReason; // 배송 취소/실패 사유 (nullable)

    @Builder
    public DeliveryResponseDto(String deliveryUuid, String orderUuid, String customerUuid, DeliveryStatus deliveryStatus, String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street, String carrier, String trackingNumber, BigDecimal deliveryFee, LocalDateTime estimatedDeliveryAt, LocalDateTime shippedAt, LocalDateTime deliveredAt, String deliveryRequest, String cancelReason) {
        this.deliveryUuid = deliveryUuid;
        this.orderUuid = orderUuid;
        this.customerUuid = customerUuid;
        this.deliveryStatus = deliveryStatus;
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.country = country;
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.deliveryFee = deliveryFee;
        this.estimatedDeliveryAt = estimatedDeliveryAt;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
        this.deliveryRequest = deliveryRequest;
        this.cancelReason = cancelReason;
    }
}
