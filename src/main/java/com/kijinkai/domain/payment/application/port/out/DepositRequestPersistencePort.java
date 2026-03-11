package com.kijinkai.domain.payment.application.port.out;


import com.kijinkai.domain.payment.adapter.out.persistence.repository.deposit.DepositSearchCondition;
import com.kijinkai.domain.payment.application.dto.DepositAdminSearchDto;
import com.kijinkai.domain.payment.application.dto.DepositAdminSummaryDto;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.payment.domain.model.DepositRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepositRequestPersistencePort {

    DepositRequest saveDepositRequest(DepositRequest request);

    Optional<DepositRequest> findByRequestUuid(UUID requestUuid);

    Optional<DepositRequest> findByCustomerUuidAndRequestUuid(UUID customerUuid, UUID reqeustUuid);

    List<DepositRequest> findByStatus(DepositStatus status);

    Page<DepositRequest> findAllByStatus(DepositStatus status, Pageable pageable);

    List<DepositRequest> saveAllDeposit(List<DepositRequest> depositRequestEntities);

    Page<DepositRequest> findByDepositPaymentUuidByStatus(@Param("customerUuid") UUID customerUuid, @Param("bankName") String bankName, @Param("status")DepositStatus status, Pageable pageable);

    Page<DepositRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);

    Page<DepositAdminSearchDto> searchDeposit(DepositSearchCondition condition, Pageable pageable);

    DepositAdminSummaryDto summary(LocalDate date);




}
