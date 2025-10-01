package com.kijinkai.domain.delivery.application.dto;

import com.kijinkai.domain.delivery.adpater.out.persistence.entity.Carrier;
import com.kijinkai.domain.delivery.adpater.out.persistence.entity.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DeliveryResponseDto {


    @Schema(description = "배송 고유 식별자", example = "xxxx-xxxx")
    private UUID deliveryUuid;

    @Schema(description = "주문 고유 식별자", example = "xxxx-xxxx")
    private UUID orderUuid;

    @Schema(description = "고객 고유 식별자", example = "xxxx-xxxx")
    private UUID customerUuid;

    @Schema(description = "배송 상태", example = "배송중")
    private DeliveryStatus deliveryStatus;

    // --- 배송 주소 스냅샷 (Snapshot) ---

    @Schema(description = "수취인", example = "Park Jinhee")
    private String recipientName;

    @Schema(description = "수취인 전화번호", example = "010-1111-1234")
    private String recipientPhoneNumber;

    @Schema(description = "도착지 국가", example = "Korea")
    private String country;

    @Schema(description = "우편번호", example = "111-111")
    private String zipcode;

    @Schema(description = "주소1", example = "대구광역시")
    private String state;

    @Schema(description = "주소2", example = "동구")
    private String city;

    @Schema(description = "주소3", example = "신암동 111-11")
    private String street;
    // ------------------------------------

    @Schema(description = "배송회사", example = "JAPANPOST")
    private Carrier carrier;

    @Schema(description = "추적번호", example = "1111-1111")
    private String trackingNumber;

    @Schema(description = "배송비", example = "50000")
    private BigDecimal deliveryFee;

    @Schema(description = "예상 배송 완료 일시", example = "2025/07/11")
    private LocalDateTime estimatedDeliveryAt; //

    @Schema(description = "실제 발송 일시", example = "2025/07/1")
    private LocalDateTime shippedAt;

    @Schema(description = "실제 배송 완료 일시", example = "2025/07/21")
    private LocalDateTime deliveredAt;

    @Schema(description = "배송 요청사항", example = "피규어를 분해해서 공간을 활용해주세요.")
    private String deliveryRequest;

    @Schema(description = "배송 취소/실패 사유", example = "고객이 배송취소 요청")
    private String cancelReason;

    @Builder
    public DeliveryResponseDto(UUID deliveryUuid, UUID orderUuid, UUID customerUuid, DeliveryStatus deliveryStatus, String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street, Carrier carrier, String trackingNumber, BigDecimal deliveryFee, LocalDateTime estimatedDeliveryAt, LocalDateTime shippedAt, LocalDateTime deliveredAt, String deliveryRequest, String cancelReason) {
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
