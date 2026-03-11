package com.kijinkai.domain.payment.adapter.out.persistence.repository.deposit;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.DepositRequestJpaEntity;
import com.kijinkai.domain.payment.application.dto.DepositAdminSearchDto;
import com.kijinkai.domain.payment.application.dto.DepositAdminSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SpringDataJpaDepositRequestRepositoryCustom {

    DepositAdminSummaryDto summary(LocalDate date);

    Page<DepositAdminSearchDto> searchDeposit(DepositSearchCondition condition, Pageable pageable);
}
