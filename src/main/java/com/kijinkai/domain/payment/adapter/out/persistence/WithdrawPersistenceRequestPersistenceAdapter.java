package com.kijinkai.domain.payment.adapter.out.persistence;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.WithdrawRequestJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.mapper.WithdrawRequestPersistenceMapper;
import com.kijinkai.domain.payment.adapter.out.persistence.repository.SpringDataJpaWithdrawRequestRepository;
import com.kijinkai.domain.payment.application.port.out.WithdrawPersistenceRequestPort;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import com.kijinkai.domain.payment.domain.model.WithdrawRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WithdrawPersistenceRequestPersistenceAdapter implements WithdrawPersistenceRequestPort {

    private final WithdrawRequestPersistenceMapper withdrawRequestPersistenceMapper;
    private final SpringDataJpaWithdrawRequestRepository withdrawRequestRepository;

    @Override
    public Optional<WithdrawRequest> findByRequestUuid(UUID requestUuid) {

        return withdrawRequestRepository.findByRequestUuid(requestUuid)
                .map(withdrawRequestPersistenceMapper::toWithdrawRequest);

    }

    @Override
    public Optional<WithdrawRequest> findByRequestUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid) {
        return withdrawRequestRepository.findByRequestUuidAndCustomerUuid(requestUuid, customerUuid)
                .map(withdrawRequestPersistenceMapper::toWithdrawRequest);
    }

    @Override
    public WithdrawRequest saveWithdrawRequest(WithdrawRequest request) {
        WithdrawRequestJpaEntity withdrawRequestJpaEntity = withdrawRequestPersistenceMapper.toWithdrawRequestJpaEntity(request);
        withdrawRequestJpaEntity = withdrawRequestRepository.save(withdrawRequestJpaEntity);
        return withdrawRequestPersistenceMapper.toWithdrawRequest(withdrawRequestJpaEntity);
    }

    @Override
    public Page<WithdrawRequest> findAllByWithdrawStatus(String bankName, WithdrawStatus status, Pageable pageable) {
        return withdrawRequestRepository.findByWithdrawPaymentUuidByStatus(bankName,status,pageable)
                .map(withdrawRequestPersistenceMapper::toWithdrawRequest);

    }

    @Override
    public Page<WithdrawRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable) {
        return withdrawRequestRepository.findAllByCustomerUuid(customerUuid, pageable)
                .map(withdrawRequestPersistenceMapper::toWithdrawRequest);
    }
}
