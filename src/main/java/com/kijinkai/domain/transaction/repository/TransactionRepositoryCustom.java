package com.kijinkai.domain.transaction.repository;

import com.kijinkai.domain.transaction.dto.TransactionAdminSummaryDto;
import com.kijinkai.domain.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;


public interface TransactionRepositoryCustom {

    Page<Transaction> search(TransactionSearchCondition condition, Pageable pageable);

    Page<Transaction> searchByAdmin(TransactionSearchCondition condition, Pageable pageable);

    TransactionAdminSummaryDto getTransactionSummary(LocalDate date);
}
