package com.kijinkai.domain.shipment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ShipmentResponseDto {

    String boxCodes;
    Double weight;
    BigDecimal shipmentFee;
    String trackingNo;

}
