package com.kijinkai.domain.shipment.dto.shipmentBoxItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StartShipmentRequestDto {

    String trackingNo;
}
