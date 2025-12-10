package com.kijinkai.domain.payment.application.port.in.orderPayment;

import com.kijinkai.domain.payment.application.dto.response.OrderPaymentCountResponseDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetOrderPaymentUseCase {
    OrderPaymentResponseDto getOrderPaymentInfoByAdmin(UUID adminUuid, UUID paymentUuid);
    OrderPaymentResponseDto getOrderPaymentInfo(UUID userUuid, UUID paymentUuid);
    Page<OrderPaymentResponseDto> getOrderPaymentsByStatusAndType(UUID userUuid, OrderPaymentStatus status, PaymentType paymentType, Pageable pageable);
    Page<OrderPaymentResponseDto> getOrderPayments(UUID adminUuid, Pageable pageable);
    OrderPaymentCountResponseDto getOrderPaymentDashboardCount(UUID userUuid);
}
