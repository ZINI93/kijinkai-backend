package com.kijinkai.domain.payment.infrastructure.adapter.repository;


import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import com.kijinkai.domain.payment.domain.repository.WithdrawRequestRepository;
import com.kijinkai.domain.payment.infrastructure.adapter.persistence.SpringDataJpaWithdrawRequestRepository;
import lombok.RequiredArgsConstructor;
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
        return springDataJpaWithdrawRequestRepository.findByRequestUuidAndCustomerUuid(requestUuid,customerUuid);
    }

    @Override
    public WithdrawRequest save(WithdrawRequest request) {
        return springDataJpaWithdrawRequestRepository.save(request);
    }
}
