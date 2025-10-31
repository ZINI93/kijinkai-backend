package com.kijinkai.domain.payment.adapter.out.persistence.repository;


import com.kijinkai.domain.payment.adapter.out.persistence.entity.RefundRequestJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaRefundRequestRepository extends JpaRepository<RefundRequestJpaEntity, Long> {
    Optional<RefundRequestJpaEntity> findByRefundUuid(UUID request);
    Optional<RefundRequestJpaEntity> findByRefundUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);

    Page<RefundRequestJpaEntity> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);
}
