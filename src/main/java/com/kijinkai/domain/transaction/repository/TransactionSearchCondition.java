package com.kijinkai.domain.transaction.repository;

import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class TransactionSearchCondition {


    private UUID customerUuid;
    private TransactionType type;
    private LocalDate starDate;
    private LocalDate endDate;

    // 관리자
    private String name;
    private String phoneNumber;
    private String paymentCode;
    private TransactionStatus transactionStatus;



}
