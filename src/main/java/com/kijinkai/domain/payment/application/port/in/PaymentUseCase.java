package com.kijinkai.domain.payment.application.port.in;

import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;
import com.kijinkai.domain.payment.application.dto.response.RefundResponseDto;
import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;
import com.kijinkai.domain.payment.domain.entity.OrderPayment;

import java.util.List;
import java.util.UUID;

public interface PaymentUseCase {

    // 입금 관련
    DepositRequestResponseDto processDepositRequest(UUID userUuid, DepositRequestDto requestDto);
    DepositRequestResponseDto approveDepositRequest(UUID requestUuid, UUID adminUuid, String memo);
    DepositRequestResponseDto getDepositRequestInfo(UUID requestUuid, UUID userUuid);
    DepositRequestResponseDto getDepositRequestInfoByAdmin(UUID requestUuid, UUID adminUuid);
    List<DepositRequestResponseDto> expireOldRequests();

    // 출금 관련
    WithdrawResponseDto processWithdrawRequest(UUID userUuid, WithdrawRequestDto requestDto);
    WithdrawResponseDto approveWithdrawRequest(UUID requestUuid, UUID adminUuid, String memo);
    WithdrawResponseDto getWithdrawInfo(UUID requestUuid, UUID userUuid);
    WithdrawResponseDto getWithdrawInfoByAdmin(UUID requestUuid, UUID adminUuid);

    // 환불 관련
    RefundResponseDto processRefundRequest(UUID adminUuid, UUID orderItemUuid, RefundRequestDto requestDto);
    RefundResponseDto approveRefundRequest(UUID refundUuid, UUID adminUuid, String memo);
    RefundResponseDto getRefundInfo(UUID refundUuid, UUID userUuid);
    RefundResponseDto getRefundInfoByAdmin(UUID refundUuid, UUID adminUuid);

    // 상품 결제 관련
    OrderPaymentResponseDto createFirstPayment(UUID adminUuid, UUID orderUuid);
    OrderPaymentResponseDto completeFirstPayment(UUID userUuid, UUID paymentUuid);
    OrderPaymentResponseDto createSecondPayment(UUID adminUuid, UUID orderUuid, OrderPaymentRequestDto requestDto);
    OrderPaymentResponseDto completeSecondPayment(UUID userUUid, UUID paymentUuid);
    OrderPaymentResponseDto getOrderPaymentInfoByAdmin(UUID adminUuid, UUID paymentUuid);
    OrderPaymentResponseDto getOrderPaymentInfo(UUID userUuid, UUID paymentUuid);


}
