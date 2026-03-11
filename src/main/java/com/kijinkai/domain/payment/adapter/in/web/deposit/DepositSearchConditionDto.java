package com.kijinkai.domain.payment.adapter.in.web.deposit;

import com.kijinkai.domain.payment.domain.enums.DepositMethod;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;

import java.time.LocalDate;

public record DepositSearchConditionDto(
        String depositCode,
        String name,
        DepositMethod depositMethod,
        DepositStatus depositStatus,
        LocalDate startDate,
        LocalDate endDate
) {
}


