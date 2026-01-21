package com.kijinkai.domain.delivery.adpater.out.persistence.entity;


import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.domain.model.DeliveryType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deliveries")
@Entity
public class DeliveryJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id", nullable = false, updatable = false, unique = true)
    private Long deliveryId;

    @Column(name = "delivery_uuid", nullable = false, updatable = false, unique = true)
    private UUID deliveryUuid;

    @Column(name = "order_payment_uuid", nullable = false, updatable = false)
    private UUID orderPaymentUuid;

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
    private String zipcode;

    @Column(name = "street_address", nullable = false, length = 100)
    private String streetAddress;

    @Column(name = "detail_address", nullable = false, length = 255)
    private String detailAddress;

    @Column(name = "pccc")
    private String pccc;

    // ------------------------------------  관리자 작성
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false, length = 50) // 택배사 이름
    private DeliveryType deliveryType;

    @Column(name = "delivery_request", length = 500)
    private String deliveryRequest; // 배송 요청 사항

//    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
//    private BigDecimal deliveryFee;

//    @Column(name = "estimated_delivery_at")
//    private LocalDateTime estimatedDeliveryAt; // 예상 배송 완료 일시

//    @Column(name = "shipped_at")
//    private LocalDateTime shippedAt; // 실제 발송 일시

//    @Column(name = "delivered_at")
//    private LocalDateTime deliveredAt; // 실제 배송 완료 일시


//    @Column(name = "cancel_reason", length = 255)
//    private String cancelReason; // 배송 취소/실패 사유 (nullable)


}

