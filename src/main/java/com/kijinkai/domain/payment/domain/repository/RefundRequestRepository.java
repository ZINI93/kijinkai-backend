package com.kijinkai.domain.payment.domain.repository;

import com.kijinkai.domain.payment.domain.entity.RefundRequest;

import java.util.Optional;
import java.util.UUID;

public interface RefundRequestRepository{
    Optional<RefundRequest> findByRequestUuid(UUID request);
    Optional<RefundRequest> findByRefundUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);
    RefundRequest save(RefundRequest request);
}