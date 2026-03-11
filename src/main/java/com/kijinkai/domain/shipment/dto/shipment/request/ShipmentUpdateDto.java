package com.kijinkai.domain.shipment.dto.shipment.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class ShipmentUpdateDto {
    private Double totalWeight;
    private BigDecimal shipmentFee;
}
