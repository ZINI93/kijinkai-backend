package com.kijinkai.domain.payment.domain.repository;

import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepositRequestRepository {

    Optional<DepositRequest> findByRefundUuid(UUID reqeustUuid);
    List<DepositRequest> findByStatus(DepositStatus status);
    Optional<DepositRequest> findByRefundUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);
    DepositRequest save(DepositRequest depositRequestEntity);
    List<DepositRequest> saveAll(List<DepositRequest> depositRequestEntities);
}