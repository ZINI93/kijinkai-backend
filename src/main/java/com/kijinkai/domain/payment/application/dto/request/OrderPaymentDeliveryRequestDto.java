package com.kijinkai.domain.payment.application.dto.request;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class OrderPaymentDeliveryRequestDto {

    List<String> orderItemCodes;
    List<String> boxCodes;

}
