package com.kijinkai.domain.payment.application.port.in;

import com.kijinkai.domain.payment.application.dto.request.*;
import com.kijinkai.domain.payment.application.dto.response.*;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentUseCase {

    // 입금 관련
    DepositRequestResponseDto processDepositRequest(UUID userUuid, DepositRequestDto requestDto);
    DepositRequestResponseDto approveDepositRequest(UUID requestUuid, UUID adminUuid, DepositRequestDto requestDto);
    DepositRequestResponseDto getDepositRequestInfo(UUID requestUuid, UUID userUuid);
    DepositRequestResponseDto getDepositRequestInfoByAdmin(UUID requestUuid, UUID adminUuid);
    Page<DepositRequestResponseDto> getDepositsByApprovalPending(UUID adminUuid, String depositorName, Pageable pageable);
    Page<DepositRequestResponseDto> getDeposits(UUID userUuid, Pageable pageable);
    List<DepositRequestResponseDto> expireOldRequests();

    // 출금 관련
    WithdrawResponseDto processWithdrawRequest(UUID userUuid, WithdrawRequestDto requestDto);
    WithdrawResponseDto approveWithdrawRequest(UUID requestUuid, UUID adminUuid, WithdrawRequestDto request);
    WithdrawResponseDto getWithdrawInfo(UUID requestUuid, UUID userUuid);
    WithdrawResponseDto getWithdrawInfoByAdmin(UUID requestUuid, UUID adminUuid);
    Page<WithdrawResponseDto> getWithdraws(UUID adminUuid, Pageable pageable);
    Page<WithdrawResponseDto> getWithdrawByApprovalPending(UUID adminUuid, String withdrawName, Pageable pageable);

    // 환불 관련
    RefundResponseDto processRefundRequest(UUID adminUuid, UUID orderItemUuid, RefundRequestDto requestDto);
    RefundResponseDto approveRefundRequest(UUID refundUuid, UUID adminUuid, String memo);
    RefundResponseDto getRefundInfo(UUID refundUuid, UUID userUuid);
    Page<RefundResponseDto> getRefunds(UUID adminUuid, Pageable pageable);
    RefundResponseDto getRefundInfoByAdmin(UUID refundUuid, UUID adminUuid);

    // 상품 결제 관련

    OrderPaymentResponseDto createSecondPayment(UUID adminUuid, OrderPaymentRequestDto requestDto);
    OrderPaymentResponseDto completeFirstPayment(UUID userUuid, OrderPaymentRequestDto requestDto);
    OrderPaymentResponseDto completeSecondPayment(UUID userUuid, OrderPaymentDeliveryRequestDto requestDto);
    OrderPaymentResponseDto getOrderPaymentInfoByAdmin(UUID adminUuid, UUID paymentUuid);
    OrderPaymentResponseDto getOrderPaymentInfo(UUID userUuid, UUID paymentUuid);
    Page<OrderPaymentResponseDto> getOrderPaymentsByStatusAndType(UUID userUuid, OrderPaymentStatus status, PaymentType paymentType, Pageable pageable);
    Page<OrderPaymentResponseDto> getOrderPayments(UUID adminUuid, Pageable pageable);
    OrderPaymentCountResponseDto getOrderPaymentDashboardCount(UUID userUuid);


}
