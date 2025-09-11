package com.kijinkai.domain.payment.infrastructure.adapter.repository;


import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import com.kijinkai.domain.payment.domain.repository.WithdrawRequestRepository;
import com.kijinkai.domain.payment.infrastructure.adapter.persistence.SpringDataJpaWithdrawRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaWithdrawRequestRepository implements WithdrawRequestRepository {

    private final SpringDataJpaWithdrawRequestRepository springDataJpaWithdrawRequestRepository;


    @Override
    public Optional<WithdrawRequest> findByRequestUuid(UUID requestUuid) {
        return springDataJpaWithdrawRequestRepository.findByRequestUuid(requestUuid);
    }

    @Override
    public Optional<WithdrawRequest> findByRequestUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid) {
        return springDataJpaWithdrawRequestRepository.findByRequestUuidAndCustomerUuid(requestUuid, customerUuid);
    }

    @Override
    public WithdrawRequest save(WithdrawRequest request) {
        return springDataJpaWithdrawRequestRepository.save(request);
    }

    @Override
    public Page<WithdrawRequest> findByWithdrawPaymentUuidByStatus(UUID custoemrUuid, String bankName, WithdrawStatus status, Pageable pageable) {
        return springDataJpaWithdrawRequestRepository.findByWithdrawPaymentUuidByStatus(custoemrUuid, bankName, status, pageable);
    }

    @Override
    public Page<WithdrawRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable) {
        return springDataJpaWithdrawRequestRepository.findAllByCustomerUuid(customerUuid,pageable);
    }
}
