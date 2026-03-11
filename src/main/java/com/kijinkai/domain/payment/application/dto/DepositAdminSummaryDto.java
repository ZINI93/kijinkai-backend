package com.kijinkai.domain.payment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;


@AllArgsConstructor
@Getter
@Builder
public class DepositAdminSummaryDto {

    private BigDecimal completedCharge;
    private BigDecimal todayCharge;
    private BigDecimal requestCharge;
    private Long totalCount;

}
