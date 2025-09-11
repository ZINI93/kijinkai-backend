package com.kijinkai.domain.payment.application.dto.response;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderPaymentCountResponseDto {

    int  firstPending;
    int  firstCompleted;

    int  secondPending;
    int  secondCompleted;
}
