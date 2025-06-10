package com.kijinkai.domain.delivery.dto;


import lombok.Getter;

@Getter
public class DeliveryUpdateDto {

    private String receiverName;
    private String postalCode;
    private String address1;
    private String address2;
    private String memo;


    public DeliveryUpdateDto(String receiverName, String postalCode, String address1, String address2, String memo) {
        this.receiverName = receiverName;
        this.postalCode = postalCode;
        this.address1 = address1;
        this.address2 = address2;
        this.memo = memo;
    }
}
