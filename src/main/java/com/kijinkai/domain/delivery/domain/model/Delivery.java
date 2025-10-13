package com.kijinkai.domain.delivery.domain.model;

import com.kijinkai.domain.delivery.application.dto.DeliveryUpdateDto;
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

    // --- 배송 주소 스냅샷 (Snapshot) ---
    private String recipientName;
    private String recipientPhoneNumber;
    private String country;
    private String zipcode;
    private String state;
    private String city;
    private String street;

    // ------------------------------------  관리자 작성

    private Carrier carrier;
    private String trackingNumber;
    private BigDecimal deliveryFee;
    private LocalDateTime estimatedDeliveryAt; // 예상 배송 완료 일시
    private LocalDateTime shippedAt; // 실제 발송 일시
    private LocalDateTime deliveredAt; // 실제 배송 완료 일시
    private String deliveryRequest; // 배송 요청 사항
    private String cancelReason; // 배송 취소/실패 사유 (nullable)


    public void updateDelivery(DeliveryUpdateDto updateDto) {

        validateDeliveryUpdate(updateDto);

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