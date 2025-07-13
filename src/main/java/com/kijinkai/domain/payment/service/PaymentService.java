package com.kijinkai.domain.payment.service;


import com.kijinkai.domain.payment.dto.PaymentDepositRequestDto;
import com.kijinkai.domain.payment.dto.PaymentResponseDto;
import com.kijinkai.domain.payment.dto.PaymentUpdateDto;
import com.kijinkai.domain.payment.dto.WithdrawalRequestDto;

import java.util.UUID;

public interface PaymentService {


    //입금
    PaymentResponseDto createDepositPayment(UUID userUuid, PaymentDepositRequestDto updateDto);
    PaymentResponseDto completeDepositPayment(UUID userUuid, String paymentUuid);

    //출금
    PaymentResponseDto createWithdrawalPayment(UUID userUuid, WithdrawalRequestDto requestDto);
    PaymentResponseDto completePaymentByWithdrawal(UUID adminUuid, String paymentUuid);

    //상품에 대한 환불
    PaymentResponseDto refundPayment(UUID adminUuid, String orderItemUuid, String reason);
    //거래 캔슬
    PaymentResponseDto cancelPayment(UUID userUuid, String paymentUuid);

    //거래 정보 조회
    PaymentResponseDto getPaymentInfo(UUID userUuid, String paymentUuid);
    PaymentResponseDto getPaymentInfoByAdmin(UUID userUuid, String paymentUuid);


}
