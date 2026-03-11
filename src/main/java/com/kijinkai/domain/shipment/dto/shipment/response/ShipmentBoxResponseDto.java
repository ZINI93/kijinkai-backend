package com.kijinkai.domain.shipment.dto.shipment.response;

import com.kijinkai.domain.shipment.dto.shipmentBoxItem.ShipmentBoxItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ShipmentBoxResponseDto {

    private UUID shipmentUuid;
    private String boxCode;
    private double weight;
    private BigDecimal shipmentFee;
    private String trackingNo;
    private List<ShipmentBoxItemResponseDto> boxItems;
}
