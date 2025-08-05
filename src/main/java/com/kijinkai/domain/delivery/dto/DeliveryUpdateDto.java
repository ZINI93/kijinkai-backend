package com.kijinkai.domain.delivery.dto;


import com.kijinkai.domain.delivery.entity.Carrier;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class DeliveryUpdateDto {


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

    @Schema(description = "배송 요청사항", example = "피규어를 분해해서 공간을 활용해주세요.")
    private String deliveryRequest; // 배송 요청 사항


    @Builder
    public DeliveryUpdateDto(String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street, Carrier carrier, String trackingNumber, BigDecimal deliveryFee, String deliveryRequest) {
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
        this.deliveryRequest = deliveryRequest;
    }
}
