package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.RefundRequestJpaEntity;
import com.kijinkai.domain.payment.domain.model.RefundRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface RefundPersistencePort {

    Optional<RefundRequest> findByRefundUuid(UUID request);
    Optional<RefundRequest> findByRefundUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);
    RefundRequest save(RefundRequest request);

    Page<RefundRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);

    Page<RefundRequest> findAll(Pageable pageable);
}
