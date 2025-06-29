package com.kijinkai.domain.payment.mapper;

import com.kijinkai.domain.payment.dto.PaymentResponseDto;
import com.kijinkai.domain.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponseDto toResponse(Payment payment){

        return PaymentResponseDto.builder()
                .paymentUuid(payment.getPaymentUuid())
                .build();
    }
}
