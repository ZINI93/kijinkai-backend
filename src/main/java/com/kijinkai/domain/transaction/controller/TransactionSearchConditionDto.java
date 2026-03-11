package com.kijinkai.domain.transaction.controller;

import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record TransactionSearchConditionDto(
        String name,
        String phoneNo,
        String paymentCode,
        TransactionStatus status,
        TransactionType type,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate

) {
}
