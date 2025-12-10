package com.kijinkai.domain.payment.adapter.out.persistence;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.DepositRequestJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.mapper.DepositRequestPersistenceMapper;
import com.kijinkai.domain.payment.application.port.out.DepositRequestPersistencePort;
import com.kijinkai.domain.payment.adapter.out.persistence.repository.SpringDataJpaDepositRequestRepository;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.payment.domain.model.DepositRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DepositRequestPersistencePersistenceAdapter implements DepositRequestPersistencePort {

    private final DepositRequestPersistenceMapper depositRequestPersistenceMapper;
    private final SpringDataJpaDepositRequestRepository springDataJpaDepositRequestRepository;

    @Override
    public DepositRequest saveDepositRequest(DepositRequest request) {
        DepositRequestJpaEntity depositRequestJapEntity = depositRequestPersistenceMapper.toDepositRequestJapEntity(request);
        depositRequestJapEntity = springDataJpaDepositRequestRepository.save(depositRequestJapEntity);
        return depositRequestPersistenceMapper.toDepositRequest(depositRequestJapEntity);
    }

    @Override
    public Optional<DepositRequest> findByRequestUuid(UUID requestUuid) {
        return springDataJpaDepositRequestRepository.findByRequestUuid(requestUuid)
                .map(depositRequestPersistenceMapper::toDepositRequest);
    }

    @Override
    public Optional<DepositRequest> findByCustomerUuidAndRequestUuid(UUID customerUuid, UUID reqeustUuid) {
        return springDataJpaDepositRequestRepository.findByRequestUuidAndCustomerUuid(reqeustUuid, customerUuid)
                .map(depositRequestPersistenceMapper::toDepositRequest);
    }

    @Override
    public List<DepositRequest> findByStatus(DepositStatus status) {
        return springDataJpaDepositRequestRepository.findByStatus(status)
                .stream().map(depositRequestPersistenceMapper::toDepositRequest)
                .toList();
    }

    @Override
    public Page<DepositRequest> findAllByStatus(DepositStatus status, Pageable pageable) {
        return springDataJpaDepositRequestRepository.findAllByStatus(status, pageable)
                .map(depositRequestPersistenceMapper::toDepositRequest);
    }

    @Override
    public List<DepositRequest> saveAllDeposit(List<DepositRequest> depositRequestEntities) {
        List<DepositRequestJpaEntity> depositRequestsJpaEntity = depositRequestPersistenceMapper.toDepositRequestsJpaEntity(depositRequestEntities);
        return springDataJpaDepositRequestRepository.saveAll(depositRequestsJpaEntity)
                .stream().map(depositRequestPersistenceMapper::toDepositRequest)
                .toList();
    }

    @Override
    public Page<DepositRequest> findByDepositPaymentUuidByStatus(UUID customerUuid, String bankName, DepositStatus status, Pageable pageable) {
        return null;
    }

    @Override
    public Page<DepositRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable) {
        return springDataJpaDepositRequestRepository.findAllByCustomerUuid(customerUuid,pageable)
                .map(depositRequestPersistenceMapper::toDepositRequest);
    }

}
