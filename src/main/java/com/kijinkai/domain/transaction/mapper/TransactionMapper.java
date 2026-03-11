package com.kijinkai.domain.transaction.mapper;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.transaction.dto.TransactionAdminSearchResponseDto;
import com.kijinkai.domain.transaction.dto.TransactionAdminSummaryDto;
import com.kijinkai.domain.transaction.dto.TransactionResponseDto;
import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.user.domain.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Component
public class TransactionMapper {

    public TransactionResponseDto toRecordResponse(Transaction transaction){

        return TransactionResponseDto.builder()
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .paymentCode(transaction.getPaymentCode())
                .transactionStatus(transaction.getTransactionStatus())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount().setScale(0, RoundingMode.HALF_UP))
                .currency(transaction.getCurrency())
                .build();
    }


    public TransactionAdminSearchResponseDto toSearchResponse(Transaction transaction, User user, Customer customer ){

        return TransactionAdminSearchResponseDto.builder()
                .transactionUuid(transaction.getTransactionUuid())
                .transactionStatus(transaction.getTransactionStatus())
                .transactionType(transaction.getTransactionType())
                .paymentCode(transaction.getPaymentCode())
                .name(customer.getLastName() + customer.getFirstName())
                .email(user.getEmail())
                .amount(transaction.getAmount())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    public TransactionAdminSummaryDto toSummaryResponse(BigDecimal totalPaidAmount, BigDecimal waitingAmount, BigDecimal refundAmount, Long totalCount ){

        return TransactionAdminSummaryDto.builder()
                .totalPaidAmount(totalPaidAmount)
                .waitingAmount(waitingAmount)
                .refundAmount(refundAmount)
                .totalCount(totalCount)
                .build();
    }
}


//결제완료 페이지 -> 관리자가 주문하고 -> 주문버튼 , 환불하고 실패버튼도 포함시켜야함