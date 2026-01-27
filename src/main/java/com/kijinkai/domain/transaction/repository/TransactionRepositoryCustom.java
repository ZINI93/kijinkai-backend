package com.kijinkai.domain.transaction.repository;

import com.kijinkai.domain.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TransactionRepositoryCustom {

    Page<Transaction> search(TransactionSearchCondition condition, Pageable pageable);

}
