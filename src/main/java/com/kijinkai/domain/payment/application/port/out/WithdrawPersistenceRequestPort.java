package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import com.kijinkai.domain.payment.domain.model.WithdrawRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface WithdrawPersistenceRequestPort {


    Optional<WithdrawRequest> findByRequestUuid(UUID requestUuid);
    Optional<WithdrawRequest> findByRequestUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);
    WithdrawRequest saveWithdrawRequest(WithdrawRequest request);

    Page<WithdrawRequest> findAllByStatus(WithdrawStatus status, Pageable pageable);
    Page<WithdrawRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);

}
