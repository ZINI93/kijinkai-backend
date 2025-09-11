package com.kijinkai.domain.payment.application.mapper;

import com.kijinkai.domain.payment.application.dto.response.*;
import com.kijinkai.domain.payment.domain.entity.*;
import com.kijinkai.domain.payment.application.dto.PaymentResponseDto;
import com.kijinkai.domain.payment.domain.service.OrderPaymentService;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
public class PaymentMapper {

    public DepositRequestResponseDto createDepositResponse(DepositRequest request){

        return DepositRequestResponseDto.builder()
                .requestUuid(request.getRequestUuid())
                .customerUuid(request.getCustomerUuid())
                .walletUuid(request.getWalletUuid())
                .amountOriginal(request.getAmountOriginal())
                .currencyOriginal(request.getCurrencyOriginal())
                .amountConverted(request.getAmountConverted())
                .exchangeRate(request.getExchangeRate())
                .depositorName(request.getDepositorName())
                .bankAccount(request.getBankAccount())
                .expiresAt(request.getExpiresAt())
                .processedByAdmin(request.getProcessedByAdmin())
                .processedAt(request.getProcessedAt())
                .adminMemo(request.getAdminMemo())
                .rejectionReason(request.getRejectionReason())
                .build();
    }

    public DepositRequestResponseDto approveDepositResponse(DepositRequest request, WalletResponseDto walletResponseDto){

        return DepositRequestResponseDto.builder()
                .requestUuid(request.getRequestUuid())
                .customerUuid(request.getCustomerUuid())
                .walletUuid(walletResponseDto.getWalletUuid())
                .amountOriginal(request.getAmountOriginal())
                .currencyOriginal(request.getCurrencyOriginal())
                .amountConverted(request.getAmountConverted())
                .exchangeRate(request.getExchangeRate())
                .depositorName(request.getDepositorName())
                .bankAccount(request.getBankAccount())
                .expiresAt(request.getExpiresAt())
                .processedByAdmin(request.getProcessedByAdmin())
                .processedAt(request.getProcessedAt())
                .adminMemo(request.getAdminMemo())
                .rejectionReason(request.getRejectionReason())
                .build();
    }

     public DepositRequestResponseDto depositInfoResponse(DepositRequest request){

        return DepositRequestResponseDto.builder()
                .requestUuid(request.getRequestUuid())
                .customerUuid(request.getCustomerUuid())
                .walletUuid(request.getWalletUuid())
                .amountOriginal(request.getAmountOriginal())
                .currencyOriginal(request.getCurrencyOriginal())
                .amountConverted(request.getAmountConverted())
                .exchangeRate(request.getExchangeRate())
                .depositorName(request.getDepositorName())
                .bankAccount(request.getBankAccount())
                .expiresAt(request.getExpiresAt())
                .processedByAdmin(request.getProcessedByAdmin())
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
                .convertedAmount(withdrawRequest.getConvertedAmount())
                .exchangeRate(withdrawRequest.getExchangeRate())
                .bankName(withdrawRequest.getBankName())
                .accountNumber(withdrawRequest.getAccountNumber())
                .status(withdrawRequest.getStatus())
                .accountHolder(withdrawRequest.getAccountHolder())
                .processedByAdmin(withdrawRequest.getProcessedByAdmin())
                .processedAt(withdrawRequest.getProcessedAt())
                .adminMemo(withdrawRequest.getAdminMemo())
                .rejectionReason(withdrawRequest.getRejectionReason())
                .build();
    }

    public WithdrawResponseDto approvedWithdrawResponse(WithdrawRequest withdrawRequest, WalletResponseDto walletResponseDto) {

        return WithdrawResponseDto.builder()
                .requestUuid(withdrawRequest.getRequestUuid())
                .customerUuid(withdrawRequest.getCustomerUuid())
                .walletUuid(walletResponseDto.getWalletUuid())
                .requestAmount(withdrawRequest.getRequestAmount())
                .withdrawFee(withdrawRequest.getWithdrawFee())
                .totalDeductAmount(withdrawRequest.getTotalDeductAmount())
                .targetCurrency(withdrawRequest.getTargetCurrency())
                .convertedAmount(withdrawRequest.getConvertedAmount())
                .exchangeRate(withdrawRequest.getExchangeRate())
                .bankName(withdrawRequest.getBankName())
                .accountNumber(withdrawRequest.getAccountNumber())
                .status(withdrawRequest.getStatus())
                .accountHolder(withdrawRequest.getAccountHolder())
                .processedByAdmin(withdrawRequest.getProcessedByAdmin())
                .processedAt(withdrawRequest.getProcessedAt())
                .adminMemo(withdrawRequest.getAdminMemo())
                .rejectionReason(withdrawRequest.getRejectionReason())
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
                .convertedAmount(withdrawRequest.getConvertedAmount())
                .exchangeRate(withdrawRequest.getExchangeRate())
                .bankName(withdrawRequest.getBankName())
                .accountNumber(withdrawRequest.getAccountNumber())
                .status(withdrawRequest.getStatus())
                .accountHolder(withdrawRequest.getAccountHolder())
                .processedByAdmin(withdrawRequest.getProcessedByAdmin())
                .processedAt(withdrawRequest.getProcessedAt())
                .adminMemo(withdrawRequest.getAdminMemo())
                .rejectionReason(withdrawRequest.getRejectionReason())
                .build();
    }

    public RefundResponseDto createRefundResponse(RefundRequest refundRequest){

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

    public RefundResponseDto processRefundResponse(RefundRequest refundRequest, WalletResponseDto walletResponseDto){

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

    public RefundResponseDto refundInfoResponse(RefundRequest refundRequest){

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


    public OrderPaymentResponseDto createOrderPayment(OrderPayment orderPayment){

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

    public OrderPaymentResponseDto completeOrderPayment(OrderPayment orderPayment, WalletResponseDto wallet){

        return OrderPaymentResponseDto.builder()
                .paymentUuid(orderPayment.getPaymentUuid())
                .customerUuid(orderPayment.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .orderUuid(orderPayment.getOrderUuid())
                .paymentAmount(orderPayment.getPaymentAmount())
                .status(orderPayment.getOrderPaymentStatus())
                .paidAt(orderPayment.getPaidAt())
                .build();

    }


    public OrderPaymentResponseDto orderPaymentInfo(OrderPayment orderPayment){

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
    ){

        return OrderPaymentCountResponseDto.builder()
                .firstPending(firstPending)
                .firstCompleted(firstCompleted)
                .secondPending(secondPending)
                .secondCompleted(secondCompleted)
                .build();
    }


    // payment details mapper

    public OrderPaymentResponseDto orderPaymentDetailsInfo(OrderPayment orderPayment, BigDecimal walletBalance){

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

    public DepositRequestResponseDto depositDetailsInfo(DepositRequest depositRequest, BigDecimal walletBalance){

        return DepositRequestResponseDto.builder()
                .requestUuid(depositRequest.getRequestUuid())
                .customerUuid(depositRequest.getCustomerUuid())
                .amountConverted(depositRequest.getAmountConverted())
                .status(depositRequest.getStatus())
                .processedAt(depositRequest.getProcessedAt())
                .createAt(depositRequest.getCreatedAt())
                .afterBalance(walletBalance.add(depositRequest.getAmountConverted()))
                .type("입금")
                .build();
    }


    public WithdrawResponseDto withdrawDetailsInfo(WithdrawRequest withdrawRequest, BigDecimal walletBalance){

        return WithdrawResponseDto.builder()
                .requestUuid(withdrawRequest.getRequestUuid())
                .customerUuid(withdrawRequest.getCustomerUuid())
                .requestAmount(withdrawRequest.getRequestAmount())
                .status(withdrawRequest.getStatus())
                .processedAt(withdrawRequest.getProcessedAt())
                .createAt(withdrawRequest.getCreatedAt())
                .afterBalance(walletBalance.subtract(withdrawRequest.getRequestAmount()))
                .type("출금")
                .build();
    }

    public RefundResponseDto refundDetailsInfo(RefundRequest refundRequest, BigDecimal walletBalance){

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
