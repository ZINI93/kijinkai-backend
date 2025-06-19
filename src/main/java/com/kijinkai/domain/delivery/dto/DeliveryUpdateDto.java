package com.kijinkai.domain.delivery.dto;


import com.kijinkai.domain.delivery.entity.Carrier;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class DeliveryUpdateDto {


    // --- 배송 주소 스냅샷 (Snapshot) ---
    private String recipientName;
    private String recipientPhoneNumber;
    private String country;
    private String zipcode;
    private String state;
    private String city;
    private String street;
    // ------------------------------------

    private Carrier carrier;
    private String trackingNumber;
    private BigDecimal deliveryFee;
    private String deliveryRequest; // 배송 요청 사항

}
