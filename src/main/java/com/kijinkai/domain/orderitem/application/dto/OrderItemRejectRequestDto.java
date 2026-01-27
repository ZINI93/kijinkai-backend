package com.kijinkai.domain.orderitem.application.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderItemRejectRequestDto {

    String rejectReason;

}
