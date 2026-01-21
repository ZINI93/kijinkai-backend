package com.kijinkai.domain.shipment.entity;


public enum ShipmentStatus {

    PAYMENT_PENDING("결제 대기중", "박스에 대한 결제 대기중" ),
    PREPARING("결제 완료", "박스가 결제되고 배송 대기중" ),
    SHIPPED("배송시작", "원하는 배송타입으로 배송시작" ),
    DELIVERED("배송완료", "구매자가 물건을 수취함" );

    private final String displayName;
    private final String description;


    ShipmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
