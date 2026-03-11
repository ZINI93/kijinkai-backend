package com.kijinkai.domain.payment.application.dto;


import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.DepositMethod;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Data
public class DepositAdminSearchDto {

    private UUID depositUuid;
    private UUID customerUuid;
    private String depositCode;
    private String name;
    private String email;
    private BigDecimal depositAmount;
    private DepositMethod depositMethod;
    private String depositor;
    private BankType bankType;
    private DepositStatus depositStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @QueryProjection
    public DepositAdminSearchDto(UUID depositUuid, UUID customerUuid, String depositCode, String name, String email, BigDecimal depositAmount, DepositMethod depositMethod, String depositor, BankType bankType, DepositStatus depositStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.depositUuid = depositUuid;
        this.customerUuid = customerUuid;
        this.depositCode = depositCode;
        this.name = name;
        this.email = email;
        this.depositAmount = depositAmount;
        this.depositMethod = depositMethod;
        this.depositor = depositor;
        this.bankType = bankType;
        this.depositStatus = depositStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
