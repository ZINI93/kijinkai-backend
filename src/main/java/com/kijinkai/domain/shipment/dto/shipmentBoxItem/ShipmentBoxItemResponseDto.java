package com.kijinkai.domain.shipment.dto.shipmentBoxItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ShipmentBoxItemResponseDto {

    String orderItemCode;
    String productLink;
    BigDecimal price;
    int quantity;

}
