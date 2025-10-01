package com.kijinkai.domain.delivery.application.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class DeliveryCountResponseDto {

    int shippedCount;
    int deliveredCount;
}
