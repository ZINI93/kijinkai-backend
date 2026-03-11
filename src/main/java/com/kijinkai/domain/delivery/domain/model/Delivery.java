package com.kijinkai.domain.delivery.domain.model;

import com.kijinkai.domain.delivery.application.dto.request.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.domain.exception.DeliveryInvalidException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    private Long deliveryId;
    private UUID deliveryUuid;
    private UUID orderPaymentUuid;
    private UUID customerUuid;
    private DeliveryStatus deliveryStatus;
    private BigDecimal totalShipmentFee;

    // --- 배송 주소 스냅샷 (Snapshot) ---

    private String recipientName;
    private String recipientPhoneNumber;
    private String zipcode;
    private String streetAddress;
    private String detailAddress;
    private String pccc;

    // ------------------------------------  관리자 작성

    private DeliveryType deliveryType;
    private String deliveryRequest; // 배송 요청 사항

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String cancelReason;

    // -- 업데이트

    public void changeShipped() {
        if (this.deliveryStatus != DeliveryStatus.PAID) {
            throw new DeliveryInvalidException("배송비가 지불된 경우에만 배송이 가능합니다.");
        }

        this.deliveryStatus = DeliveryStatus.SHIPPED;

    }

    public void changeCancel(String cancelReason) {
        if (this.deliveryStatus != DeliveryStatus.PENDING && this.deliveryStatus != DeliveryStatus.PACKED) {
            throw new DeliveryInvalidException("대기중이거나, 포장상태에서만 취소가 가능합니다.");
        }

        if (cancelReason == null || cancelReason.isEmpty()){
            throw new DeliveryInvalidException("취소 이유는 필수입니다.");
        }

        this.cancelReason = cancelReason;
        this.deliveryStatus = DeliveryStatus.CANCELLED;
    }

    public void changeTotalShipmentFee(BigDecimal totalShipmentFee) {
        if (this.deliveryStatus != DeliveryStatus.PACKED && this.deliveryStatus != DeliveryStatus.REQUEST_PAYMENT) {
            throw new DeliveryInvalidException("배송 준비 상태이거나, 배송지불요청 상태에서만 가격변경이 됩니다. 지불하면 변경불가");
        }
        this.totalShipmentFee = totalShipmentFee;
    }

    public void changeRequestPayment(BigDecimal totalShipmentFee) {
        if (this.deliveryStatus != DeliveryStatus.PACKED) {
            throw new DeliveryInvalidException("배송 준비 상태에서만 지불 요청을 할 수 있습니다.");
        }

        this.totalShipmentFee = totalShipmentFee;
        this.deliveryStatus = DeliveryStatus.REQUEST_PAYMENT;
    }

    public void changePending() {
        if (this.deliveryStatus != DeliveryStatus.PACKED && this.deliveryStatus != DeliveryStatus.CANCELLED
        && this.deliveryStatus != DeliveryStatus.REQUEST_PAYMENT) {
            throw new DeliveryInvalidException("포장만 완료된 상태에거나, 캔슬된 상태에서만 보류로 복귀가 가능합니다.");
        }

        this.deliveryStatus = DeliveryStatus.PENDING;
    }

    public void completedPacking() {

        if (this.deliveryStatus != DeliveryStatus.PENDING) {
            throw new DeliveryInvalidException("대시중인 배송요청만 패킹이 가능합니다.");
        }

        this.deliveryStatus = DeliveryStatus.PACKED;
    }

    public void updateDelivery(DeliveryUpdateDto updateDto) {

        validateDeliveryUpdate(updateDto);

        this.recipientName = updateDto.getRecipientName();
        this.recipientPhoneNumber = updateDto.getRecipientPhoneNumber();
        this.zipcode = updateDto.getZipcode();
    }

    private void validateDeliveryUpdate(DeliveryUpdateDto updateDto) {
        if (updateDto == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        if (this.deliveryStatus != DeliveryStatus.PENDING) {
            throw new DeliveryInvalidException("Cannot update delivery. Status must be PENDING");
        }
    }

    public void updateDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}