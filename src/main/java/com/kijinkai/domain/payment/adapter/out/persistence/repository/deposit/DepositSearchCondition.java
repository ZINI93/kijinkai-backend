package com.kijinkai.domain.payment.adapter.out.persistence.repository.deposit;

import com.kijinkai.domain.payment.domain.enums.DepositMethod;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DepositSearchCondition {

    private String depositCode;
    private String name;
    private DepositMethod depositMethod;
    private DepositStatus depositStatus;
    private LocalDate startDate;
    private LocalDate endDate;

}
