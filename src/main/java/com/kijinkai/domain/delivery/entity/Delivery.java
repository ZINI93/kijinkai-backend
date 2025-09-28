package com.kijinkai.domain.delivery.entity;


import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.dto.DeliveryUpdateDto;
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
@Table(name = "deliveries")
@Entity
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id", nullable = false, updatable = false, unique = true)
    private Long deliveryId;

    @Column(name = "delivery_uuid", nullable = false, updatable = false, unique = true)
    private UUID deliveryUuid;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id", nullable = false, updatable = false)
//    private Order order;  // 필요 없음 // order_payment를 참조

    @Column(name = "order_payment_uuid", nullable = false, updatable = false)
    private UUID orderPaymentUuid;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
//    private Customer customer;

    @Column(name = "customer_uuid", nullable = false)
    private UUID customerUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    // --- 배송 주소 스냅샷 (Snapshot) ---
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_phone_number", nullable = false)
    private String recipientPhoneNumber;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    // ------------------------------------  관리자 작성
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50) // 택배사 이름
    private Carrier carrier;

    @Column(name = "tracking_number", unique = true, nullable = false, length = 100) // 송장 번호
    private String trackingNumber;

    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(name = "estimated_delivery_at")
    private LocalDateTime estimatedDeliveryAt; // 예상 배송 완료 일시

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt; // 실제 발송 일시

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt; // 실제 배송 완료 일시

    @Column(name = "delivery_request", length = 500)
    private String deliveryRequest; // 배송 요청 사항

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason; // 배송 취소/실패 사유 (nullable)


    @Builder
    public Delivery(UUID deliveryUuid, UUID orderPaymentUuid, UUID customerUuid, DeliveryStatus deliveryStatus, String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street, Carrier carrier, String trackingNumber, BigDecimal deliveryFee, LocalDateTime estimatedDeliveryAt, LocalDateTime shippedAt, LocalDateTime deliveredAt, String deliveryRequest, String cancelReason) {
        this.deliveryUuid = deliveryUuid != null ? deliveryUuid : UUID.randomUUID();
        this.orderPaymentUuid = orderPaymentUuid;
        this.customerUuid = customerUuid;
        this.deliveryStatus = deliveryStatus != null ? deliveryStatus : DeliveryStatus.PENDING;
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

    public void updateDelivery(DeliveryUpdateDto updateDto) {
        this.recipientName = updateDto.getRecipientName();
        this.recipientPhoneNumber = updateDto.getRecipientPhoneNumber();
        this.country = updateDto.getCountry();
        this.zipcode = updateDto.getZipcode();
        this.state = updateDto.getState();
        this.city = updateDto.getCity();
        this.street = updateDto.getStreet();
        this.carrier = updateDto.getCarrier();
        this.trackingNumber = updateDto.getTrackingNumber();
        this.deliveryFee = updateDto.getDeliveryFee();
    }

    public void updateDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
