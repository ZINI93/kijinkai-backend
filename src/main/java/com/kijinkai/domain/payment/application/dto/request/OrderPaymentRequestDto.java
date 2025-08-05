package com.kijinkai.domain.payment.application.dto.request;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class OrderPaymentRequestDto {

    BigDecimal deliveryFee;





}
