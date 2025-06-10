package com.kijinkai.domain.delivery.dto;

import com.kijinkai.domain.customer.entity.Customer;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryResponseDto {

    private String deliveryUuid;
    private String customerUuid;
    private String receiverName;
    private String postalCode;
    private String address1;
    private String address2;
    private String memo;


    @Builder
    public DeliveryResponseDto(String deliveryUuid, String customerUuid, String receiverName, String postalCode, String address1, String address2, String memo) {
        this.deliveryUuid = deliveryUuid;
        this.customerUuid = customerUuid;
        this.receiverName = receiverName;
        this.postalCode = postalCode;
        this.address1 = address1;
        this.address2 = address2;
        this.memo = memo;
    }
}
