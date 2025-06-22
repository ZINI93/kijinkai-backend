package com.kijinkai.domain.payment.service;


import com.kijinkai.domain.payment.dto.PaymentRequestDto;
import com.kijinkai.domain.payment.dto.PaymentResponseDto;
import com.kijinkai.domain.payment.dto.PaymentUpdateDto;

public interface PaymentService {


    PaymentResponseDto createPaymentWithValidate(String userUuid, PaymentRequestDto updateDto);
    PaymentResponseDto completePayment(String userUuid, String paymentUuid);
    PaymentResponseDto refundPayment(String userUuid, String paymentUuid);
    PaymentResponseDto cancelPayment(String userUuid, String paymentUuid);
    PaymentResponseDto getPaymentInfo(String userUuid, String paymentUuid);

}
