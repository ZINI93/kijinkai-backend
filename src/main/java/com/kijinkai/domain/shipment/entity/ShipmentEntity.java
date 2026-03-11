package com.kijinkai.domain.shipment.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.shipment.dto.shipment.request.ShipmentUpdateDto;
import com.kijinkai.domain.shipment.dto.shipmentBoxItem.StartShipmentRequestDto;
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
    @Column(name = "order_payment_uuid", updatable = false)
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


    //업데이트
    public void updatePaid(){
        if (this.shipmentStatus != ShipmentStatus.PAYMENT_PENDING){
            throw new IllegalArgumentException("결제 대기중인 박스만 결제가 가능합니다.");
        }
        this.shipmentStatus = ShipmentStatus.PAID;
    }

    public void updatePackedShipment(ShipmentUpdateDto updateDto){
        if (updateDto.getTotalWeight() != null && updateDto.getTotalWeight() < 0) throw new IllegalArgumentException("무게 오류");
        if (updateDto.getShipmentFee() != null && updateDto.getShipmentFee().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("금액 오류");

        this.totalWeight = (updateDto.getTotalWeight() != null) ? updateDto.getTotalWeight() : this.totalWeight;
        this.shippingFee = (updateDto.getShipmentFee() != null) ? updateDto.getShipmentFee() : this.shippingFee;
    }


    //추가
    public void addTrackingNoAndChangeShipped(String trackingNo){
        if (this.shipmentStatus != ShipmentStatus.PAID){
            throw new IllegalArgumentException("결제된 박스만 배송이 가능합니다.");
        }

        if (trackingNo == null || trackingNo.trim().isEmpty()){
            throw new IllegalArgumentException("운송장 번호는 필수입니다.");
        }

        this.shipmentStatus = ShipmentStatus.SHIPPED;
        this.trackingNo = trackingNo;
    }


    public void completePayment(UUID orderPaymentUuid) {
        if (shipmentStatus != ShipmentStatus.PAYMENT_PENDING) {
            throw new IllegalArgumentException("결제 대기상태에서만 가능합니다.");
        }
        this.orderPaymentUuid = orderPaymentUuid;
        this.shipmentStatus = ShipmentStatus.PAID;
    }


    public void startShipment(StartShipmentRequestDto requestDto) {
        if (shipmentStatus != ShipmentStatus.PAID) {
            throw new IllegalArgumentException("결제 완료된 상품만 발송이 가능합니다.");
        }
        this.shipmentStatus = ShipmentStatus.SHIPPED;
        this.trackingNo = requestDto.getTrackingNo();
    }

    //상태변경
    public void delivered() {
        if (shipmentStatus != ShipmentStatus.SHIPPED) {
            throw new IllegalArgumentException("배송중인 상품만 배송완료를 할수 있습니다.");
        }

        this.shipmentStatus = ShipmentStatus.DELIVERED;
    }

}
