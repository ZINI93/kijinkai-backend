package com.kijinkai.domain.transaction.repository;

import com.kijinkai.domain.transaction.entity.TransactionType;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class TransactionSearchCondition {

    private UUID customerUuid;
    private TransactionType type;
    private LocalDate starDate;
    private LocalDate endDate;

}
