package com.kijinkai.domain.payment.infrastructure.adapter.repository;


import com.kijinkai.domain.payment.domain.entity.RefundRequest;
import com.kijinkai.domain.payment.domain.repository.RefundRequestRepository;
import com.kijinkai.domain.payment.infrastructure.adapter.persistence.SpringDataRefundRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaRefundRequestRepository implements RefundRequestRepository {

    private final SpringDataRefundRequestRepository springDataRefundRequestRepository;

    @Override
    public Optional<RefundRequest> findByRefundUuid(UUID refundUuid) {
        return springDataRefundRequestRepository.findByRefundUuid(refundUuid);
    }

    @Override
    public Optional<RefundRequest> findByRefundUuidAndCustomerUuid(UUID refundUuid, UUID customerUuid) {
        return springDataRefundRequestRepository.findByRefundUuidAndCustomerUuid(refundUuid,customerUuid);
    }

    @Override
    public RefundRequest save(RefundRequest request) {
        return springDataRefundRequestRepository.save(request);
    }
}
