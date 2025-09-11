package com.kijinkai.domain.payment.domain.repository;

import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import com.kijinkai.domain.payment.domain.entity.RefundRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface RefundRequestRepository{
    Optional<RefundRequest> findByRefundUuid(UUID request);
    Optional<RefundRequest> findByRefundUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);
    RefundRequest save(RefundRequest request);

    Page<RefundRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);

}