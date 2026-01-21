package com.kijinkai.domain.payment.application.port.in.orderPayment;

import com.kijinkai.domain.payment.application.dto.request.OrderPaymentDeliveryRequestDto;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;

import java.util.UUID;

public interface UpdateOrderPaymentUseCase {
    OrderPaymentResponseDto completeFirstPayment(UUID userUuid, OrderPaymentRequestDto requestDto);
}
