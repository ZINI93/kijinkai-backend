package com.kijinkai.domain.payment.application.port.in.orderPayment;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentDeliveryRequestDto;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;
import com.kijinkai.domain.payment.domain.model.OrderPayment;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateOrderPaymentUseCase {
    OrderPaymentResponseDto createSecondPayment(UUID userUuid, OrderPaymentRequestDto requestDto);
    OrderPaymentResponseDto paymentDeliverFee(UUID userUuid, OrderPaymentDeliveryRequestDto requestDto);
    OrderPayment saveOrderItem(Customer customer, BigDecimal exchangedAmount, BigDecimal discountAmount, BigDecimal finalPaymentAmount, UUID userCouponUuid);
}
