package com.kijinkai.domain.shipment.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.shipment.dto.StartShipmentRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "shipments")
@Entity
public class ShipmentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ship_id", nullable = false, updatable = false)
    private Long shipmentId;

    @Comment("박스 식별자")
    @Column(name = "ship_uuid", nullable = false, updatable = false)
    private UUID shipmentUuid;

    @Comment("고객 Uuid")
    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Comment("배송 Uuid")
    @Column(name = "delivery_uuid", nullable = false, updatable = false)
    private UUID deliveryUuid;

    @Comment("결제 Uuid")
    @Column(name = "order_payment_uuid", nullable = false, updatable = false)
    private UUID orderPaymentUuid;

    @Comment("박스 외부코드")
    @Column(name = "box_code", nullable = false, updatable = false)
    private String boxCode;


    @Comment("추적번호")
    @Column(name = "tracking_no")
    private String trackingNo;

    @Comment("박스 실측무게")
    @Column(name = "total_weight", nullable = false)
    private Double totalWeight;

    @Comment("배송비")
    @Column(name = "shipping_fee", nullable = false)
    private BigDecimal shippingFee;

    @Comment("박스 상태")
    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", nullable = false)
    private ShipmentStatus shipmentStatus;

    @Comment("박스에 대한 예상 도착일시")
    @Column(name = "estimated_delivery_at")
    private LocalDateTime estimatedDeliveryAt;

    @Comment("실제 발송 일시")
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Comment("발송 완료 일시")
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    //추가
    public void completePayment(UUID orderPaymentUuid) {
        if (shipmentStatus != ShipmentStatus.PAYMENT_PENDING) {
            throw new IllegalArgumentException("결제 대기상태에서만 가능합니다.");
        }
        this.orderPaymentUuid = orderPaymentUuid;
        this.shipmentStatus = ShipmentStatus.PREPARING;
    }


    public void startShipment(StartShipmentRequestDto requestDto){
        if (shipmentStatus != ShipmentStatus.PREPARING){
            throw new IllegalArgumentException("결제 완료된 상품만 발송이 가능합니다.");
        }
        this.shipmentStatus = ShipmentStatus.SHIPPED;
        this.trackingNo = requestDto.getTrackingNo();
    }

    //상태변경
    public void delivered(){
        if (shipmentStatus != ShipmentStatus.SHIPPED){
            throw new IllegalArgumentException("배송중인 상품만 배송완료를 할수 있습니다.");
        }

        this.shipmentStatus =ShipmentStatus.DELIVERED;
    }

}
