package com.kijinkai.domain.payment.application.port.in.orderPayment;

import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;

import java.util.UUID;

public interface CreateOrderPaymentUseCase {
    OrderPaymentResponseDto createSecondPayment(UUID adminUuid, OrderPaymentRequestDto requestDto);
}
