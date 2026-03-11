package com.kijinkai.domain.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;


@Builder
@Getter
@AllArgsConstructor
public class TransactionAdminSummaryDto {

    public BigDecimal totalPaidAmount; // 결제완료 금액
    public BigDecimal waitingAmount;
    public BigDecimal refundAmount;
    public Long totalCount;
}
