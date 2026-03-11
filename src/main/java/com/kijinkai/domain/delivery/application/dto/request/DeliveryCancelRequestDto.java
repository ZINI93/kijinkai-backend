package com.kijinkai.domain.delivery.application.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DeliveryCancelRequestDto {

    private String cancelReason;
}
