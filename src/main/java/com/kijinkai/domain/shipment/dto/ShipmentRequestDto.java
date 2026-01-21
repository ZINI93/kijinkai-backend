package com.kijinkai.domain.shipment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class ShipmentRequestDto {

    private List<String> orderItemCodes;
    private List<BoxInfo> boxes;


    @Getter
    @NoArgsConstructor
    public static class BoxInfo {

        private Double weight;
        private List<String> orderItemCodes;
        private BigDecimal shippingFee;
        private List<OrderItemInfo> orderItemInfos ;
    }

    @Getter
    @NoArgsConstructor
    public static class OrderItemInfo{

    private String orderItemCode;
    private int quantity;

    }

}
