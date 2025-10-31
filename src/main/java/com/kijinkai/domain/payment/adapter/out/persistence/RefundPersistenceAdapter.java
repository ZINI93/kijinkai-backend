package com.kijinkai.domain.payment.adapter.out.persistence;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.RefundRequestJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.mapper.RefundRequestPersistenceMapper;
import com.kijinkai.domain.payment.adapter.out.persistence.repository.SpringDataJpaRefundRequestRepository;
import com.kijinkai.domain.payment.application.port.out.RefundPersistencePort;
import com.kijinkai.domain.payment.domain.model.RefundRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefundPersistenceAdapter implements RefundPersistencePort {

    private final SpringDataJpaRefundRequestRepository springDataJpaRefundRequestRepository;
    private final RefundRequestPersistenceMapper refundRequestPersistenceMapper;

    @Override
    public Optional<RefundRequest> findByRefundUuid(UUID request) {
        return springDataJpaRefundRequestRepository.findByRefundUuid(request)
                .map(refundRequestPersistenceMapper::toRefundRequest);
    }

    @Override
    public Optional<RefundRequest> findByRefundUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid) {
        return springDataJpaRefundRequestRepository.findByRefundUuidAndCustomerUuid(requestUuid,customerUuid)
                .map(refundRequestPersistenceMapper::toRefundRequest);
    }

    @Override
    public RefundRequest save(RefundRequest request) {
        RefundRequestJpaEntity refundRequestJpaEntity = refundRequestPersistenceMapper.toRefundRequestJpaEntity(request);
        refundRequestJpaEntity = springDataJpaRefundRequestRepository.save(refundRequestJpaEntity);
        return refundRequestPersistenceMapper.toRefundRequest(refundRequestJpaEntity);
    }

    @Override
    public Page<RefundRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable) {
        return springDataJpaRefundRequestRepository.findAllByCustomerUuid(customerUuid,pageable)
                .map(refundRequestPersistenceMapper::toRefundRequest);
    }

    @Override
    public Page<RefundRequest> findAll(Pageable pageable) {
        return springDataJpaRefundRequestRepository.findAll(pageable)
                .map(refundRequestPersistenceMapper::toRefundRequest);
    }
}
