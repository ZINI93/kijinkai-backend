package com.kijinkai.domain.payment.infrastructure.adapter.persistence;


import com.kijinkai.domain.payment.domain.entity.RefundRequest;
import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataRefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    Optional<RefundRequest> findByRefundUuid(UUID request);
    Optional<RefundRequest> findByRefundUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);

    Page<RefundRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);

}
