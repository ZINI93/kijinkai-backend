package com.kijinkai.domain.payment.application.mapper;

import com.kijinkai.domain.payment.application.dto.response.*;
import com.kijinkai.domain.payment.domain.model.DepositRequest;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import com.kijinkai.domain.payment.domain.model.RefundRequest;
import com.kijinkai.domain.payment.domain.model.WithdrawRequest;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Component
public class PaymentMapper {

    public DepositRequestResponseDto createDepositResponse(DepositRequest request) {

        return DepositRequestResponseDto.builder()
                .depositCode(request.getDepositCode())
                .amountOriginal(request.getAmountOriginal())
                .depositorName(request.getDepositorName())
                .expiresAt(request.getExpiresAt())
                .build();
    }

    public DepositRequestResponseDto approveDepositResponse(DepositRequest request, WalletResponseDto walletResponseDto) {

        return DepositRequestResponseDto.builder()
                .requestUuid(request.getRequestUuid())
                .customerUuid(request.getCustomerUuid())
                .walletUuid(walletResponseDto.getWalletUuid())
                .amountOriginal(request.getAmountOriginal())
                .currencyOriginal(request.getCurrencyOriginal())
                .depositorName(request.getDepositorName())
                .expiresAt(request.getExpiresAt())
                .processedByAdmin(request.getProcessedByAdminUuid())
                .processedAt(request.getProcessedAt())
                .adminMemo(request.getAdminMemo())
                .rejectionReason(request.getRejectionReason())
                .type("입금")
                .build();
    }

    public DepositRequestResponseDto depositInfoResponse(DepositRequest request) {

        return DepositRequestResponseDto.builder()
                .requestUuid(request.getRequestUuid())
                .customerUuid(request.getCustomerUuid())
                .walletUuid(request.getWalletUuid())
                .amountOriginal(request.getAmountOriginal())
                .currencyOriginal(request.getCurrencyOriginal())
                .depositorName(request.getDepositorName())
                .expiresAt(request.getExpiresAt())
                .processedByAdmin(request.getProcessedByAdminUuid())
                .processedAt(request.getProcessedAt())
                .adminMemo(request.getAdminMemo())
                .rejectionReason(request.getRejectionReason())
                .build();
    }


    public WithdrawResponseDto createWithdrawResponse(WithdrawRequest withdrawRequest) {

        return WithdrawResponseDto.builder()
                .requestUuid(withdrawRequest.getRequestUuid())
                .customerUuid(withdrawRequest.getCustomerUuid())
                .walletUuid(withdrawRequest.getWalletUuid())
                .requestAmount(withdrawRequest.getRequestAmount())
                .withdrawFee(withdrawRequest.getWithdrawFee())
                .totalDeductAmount(withdrawRequest.getTotalDeductAmount())
                .targetCurrency(withdrawRequest.getTargetCurrency())
                .bankType(withdrawRequest.getBankType())
                .accountNumber(withdrawRequest.getAccountNumber())
                .status(withdrawRequest.getStatus())
                .accountHolder(withdrawRequest.getAccountHolder())
                .processedByAdmin(withdrawRequest.getProcessedByAdminUuid())
                .processedAt(withdrawRequest.getProcessedAt())
                .adminMemo(withdrawRequest.getAdminMemo())
                .rejectionReason(withdrawRequest.getRejectionReason())
                .build();
    }

    public WithdrawResponseDto approvedWithdrawResponse(WithdrawRequest withdrawRequest) {

        return WithdrawResponseDto.builder()
                .customerUuid(withdrawRequest.getCustomerUuid())
                .requestAmount(withdrawRequest.getRequestAmount())
                .withdrawFee(withdrawRequest.getWithdrawFee())
                .totalDeductAmount(withdrawRequest.getTotalDeductAmount())
                .targetCurrency(withdrawRequest.getTargetCurrency())
                .accountNumber(withdrawRequest.getAccountNumber())
                .accountHolder(withdrawRequest.getAccountHolder())
                .processedByAdmin(withdrawRequest.getProcessedByAdminUuid())
                .processedAt(withdrawRequest.getProcessedAt())
                .adminMemo(withdrawRequest.getAdminMemo())
                .build();
    }

    public WithdrawResponseDto withdrawInfoResponse(WithdrawRequest withdrawRequest) {

        return WithdrawResponseDto.builder()
                .requestUuid(withdrawRequest.getRequestUuid())
                .customerUuid(withdrawRequest.getCustomerUuid())
                .walletUuid(withdrawRequest.getWalletUuid())
                .requestAmount(withdrawRequest.getRequestAmount())
                .withdrawFee(withdrawRequest.getWithdrawFee())
                .totalDeductAmount(withdrawRequest.getTotalDeductAmount())
                .targetCurrency(withdrawRequest.getTargetCurrency())
                .bankType(withdrawRequest.getBankType())

                .accountNumber(withdrawRequest.getAccountNumber())
                .status(withdrawRequest.getStatus())
                .accountHolder(withdrawRequest.getAccountHolder())
                .processedByAdmin(withdrawRequest.getProcessedByAdminUuid())
                .processedAt(withdrawRequest.getProcessedAt())
                .adminMemo(withdrawRequest.getAdminMemo())
                .rejectionReason(withdrawRequest.getRejectionReason())
                .build();
    }

    public RefundResponseDto createRefundResponse(RefundRequest refundRequest) {

        return RefundResponseDto.builder()
                .refundUuid(refundRequest.getRefundUuid())
                .customerUuid(refundRequest.getCustomerUuid())
                .orderItemUuid(refundRequest.getOrderItemUuid())
                .refundAmount(refundRequest.getRefundAmount())
                .processedByAdmin(refundRequest.getProcessedByAdmin())
                .adminMemo(refundRequest.getAdminMemo())
                .refundReason(refundRequest.getRefundReason())
                .build();
    }

    public RefundResponseDto processRefundResponse(RefundRequest refundRequest, WalletResponseDto walletResponseDto) {

        return RefundResponseDto.builder()
                .refundUuid(refundRequest.getRefundUuid())
                .customerUuid(refundRequest.getCustomerUuid())
                .walletUuid(walletResponseDto.getWalletUuid())
                .orderItemUuid(refundRequest.getOrderItemUuid())
                .refundAmount(refundRequest.getRefundAmount())
                .processedByAdmin(refundRequest.getProcessedByAdmin())
                .adminMemo(refundRequest.getAdminMemo())
                .refundReason(refundRequest.getRefundReason())
                .build();
    }

    public RefundResponseDto refundInfoResponse(RefundRequest refundRequest) {

        return RefundResponseDto.builder()
                .refundUuid(refundRequest.getRefundUuid())
                .customerUuid(refundRequest.getCustomerUuid())
                .walletUuid(refundRequest.getWalletUuid())
                .orderItemUuid(refundRequest.getOrderItemUuid())
                .refundAmount(refundRequest.getRefundAmount())
                .processedByAdmin(refundRequest.getProcessedByAdmin())
                .adminMemo(refundRequest.getAdminMemo())
                .refundReason(refundRequest.getRefundReason())
                .build();
    }


    public OrderPaymentResponseDto deliveryPaymentResponse(OrderPayment orderPayment, BigDecimal paymentAmount, BigDecimal afterBalance) {

        // 페이먼트 코드
        return OrderPaymentResponseDto.builder()
                .orderPaymentCode(orderPayment.getOrderPaymentCode())
                .paidAt(LocalDateTime.now())
                .paymentAmount(paymentAmount)
                .afterBalance(afterBalance)
                .build();

    }


    public OrderPaymentResponseDto createOrderPayment(OrderPayment orderPayment) {

        return OrderPaymentResponseDto.builder()
                .paymentUuid(orderPayment.getPaymentUuid())
                .customerUuid(orderPayment.getCustomerUuid())
                .walletUuid(orderPayment.getWalletUuid())
                .orderUuid(orderPayment.getOrderUuid())
                .paymentAmount(orderPayment.getPaymentAmount())
                .status(orderPayment.getOrderPaymentStatus())
                .paidAt(orderPayment.getPaidAt())
                .build();

    }

    public OrderPaymentResponseDto completeOrderPayment(OrderPayment orderPayment, WalletResponseDto walletResponseDto) {

        return OrderPaymentResponseDto.builder()
                .paymentUuid(orderPayment.getPaymentUuid())
                .customerUuid(orderPayment.getCustomerUuid())
                .walletUuid(walletResponseDto.getWalletUuid())
                .orderUuid(orderPayment.getOrderUuid())
                .paymentAmount(orderPayment.getPaymentAmount())
                .status(orderPayment.getOrderPaymentStatus())
                .paidAt(orderPayment.getPaidAt())
                .build();

    }


    public OrderPaymentResponseDto orderPaymentInfo(OrderPayment orderPayment) {

        return OrderPaymentResponseDto.builder()
                .paymentUuid(orderPayment.getPaymentUuid())
                .customerUuid(orderPayment.getCustomerUuid())
                .walletUuid(orderPayment.getWalletUuid())
                .orderUuid(orderPayment.getOrderUuid())
                .paymentAmount(orderPayment.getPaymentAmount())
                .status(orderPayment.getOrderPaymentStatus())
                .paidAt(orderPayment.getPaidAt())
                .build();

    }


    public OrderPaymentCountResponseDto orderPaymentDashboardCount(
            int firstPending, int firstCompleted, int secondPending, int secondCompleted
    ) {

        return OrderPaymentCountResponseDto.builder()
                .firstPending(firstPending)
                .firstCompleted(firstCompleted)
                .secondPending(secondPending)
                .secondCompleted(secondCompleted)
                .build();
    }


    // payment details mapper

    public OrderPaymentResponseDto orderPaymentDetailsInfo(OrderPayment orderPayment, BigDecimal walletBalance) {

        return OrderPaymentResponseDto.builder()
                .paymentUuid(orderPayment.getPaymentUuid())
                .customerUuid(orderPayment.getCustomerUuid())
                .paymentAmount(orderPayment.getPaymentAmount())
                .status(orderPayment.getOrderPaymentStatus())
                .paidAt(orderPayment.getPaidAt())
                .createAt(orderPayment.getCreatedAt())
                .afterBalance(walletBalance.add(orderPayment.getPaymentAmount()))
                .type("구매")
                .build();
    }

    public DepositRequestResponseDto depositDetailsInfo(DepositRequest depositRequest, BigDecimal walletBalance) {

        return DepositRequestResponseDto.builder()
                .requestUuid(depositRequest.getRequestUuid())
                .customerUuid(depositRequest.getCustomerUuid())
                .status(depositRequest.getStatus())
                .processedAt(depositRequest.getProcessedAt())
                .createAt(depositRequest.getCreatedAt())
                .type("입금")
                .build();
    }


    public DepositRequestResponseDto depositInfo(DepositRequest depositRequest) {

        return DepositRequestResponseDto.builder()
                .requestUuid(depositRequest.getRequestUuid())
                .customerUuid(depositRequest.getCustomerUuid())
                .status(depositRequest.getStatus())
                .adminMemo(depositRequest.getAdminMemo())
                .amountOriginal(depositRequest.getAmountOriginal())
                .depositorName(depositRequest.getDepositorName())
                .processedByAdmin(depositRequest.getProcessedByAdminUuid())
                .processedAt(depositRequest.getProcessedAt())
                .createAt(depositRequest.getCreatedAt())
                .build();
    }


    public WithdrawResponseDto withdrawDetailsInfo(WithdrawRequest withdrawRequest, BigDecimal walletBalance) {

        return WithdrawResponseDto.builder()
                .requestUuid(withdrawRequest.getRequestUuid())
                .customerUuid(withdrawRequest.getCustomerUuid())
                .requestAmount(withdrawRequest.getRequestAmount())
                .status(withdrawRequest.getStatus())
                .processedAt(withdrawRequest.getProcessedAt())
                .createdAt(withdrawRequest.getCreatedAt())
                .updatedAt(withdrawRequest.getUpdatedAt())
                .afterBalance(walletBalance.subtract(withdrawRequest.getRequestAmount()))
                .type("출금")
                .build();
    }

    public RefundResponseDto refundDetailsInfo(RefundRequest refundRequest, BigDecimal walletBalance) {

        return RefundResponseDto.builder()
                .refundUuid(refundRequest.getRefundUuid())
                .customerUuid(refundRequest.getCustomerUuid())
                .refundAmount(refundRequest.getRefundAmount())
                .status(refundRequest.getStatus())
                .processedAt(refundRequest.getProcessedAt())
                .createAt(refundRequest.getCreatedAt())
                .afterBalance(walletBalance.subtract(refundRequest.getRefundAmount()))
                .type("환불")
                .build();
    }

}
