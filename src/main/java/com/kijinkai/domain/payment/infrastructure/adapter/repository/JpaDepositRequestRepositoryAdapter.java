package com.kijinkai.domain.payment.infrastructure.adapter.repository;


import com.kijinkai.domain.payment.application.port.out.DepositRequestPort;
import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.payment.domain.repository.DepositRequestRepository;
import com.kijinkai.domain.payment.infrastructure.adapter.persistence.SpringDataJpaDepositRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaDepositRequestRepositoryAdapter implements DepositRequestRepository, DepositRequestPort {


    private final SpringDataJpaDepositRequestRepository springDataJpaDepositRequestRepository;

    @Override
    public Optional<DepositRequest> findByRefundUuid(UUID reqeustUuid) {
        return springDataJpaDepositRequestRepository.findByRequestUuid(reqeustUuid);
    }

    @Override
    public List<DepositRequest> findByStatus(DepositStatus status) {
        return springDataJpaDepositRequestRepository.findByStatus(status);
    }

    @Override
    public Optional<DepositRequest> findByRefundUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid) {
        return springDataJpaDepositRequestRepository.findByRequestUuidAndCustomerUuid(requestUuid,customerUuid);
    }

    @Override
    public DepositRequest save(DepositRequest depositRequestEntity) {
        return springDataJpaDepositRequestRepository.save(depositRequestEntity);
    }

    @Override
    public DepositRequest findDepositRequest(UUID requestUuid) {
        return null;
    }

    @Override
    public List<DepositRequest> saveAll(List<DepositRequest> depositRequestEntity) {
        return springDataJpaDepositRequestRepository.saveAll(depositRequestEntity);
    }

    @Override
    public Page<DepositRequest> findByDepositPaymentUuidByStatus(UUID customerUuid, String depositorName, DepositStatus status, Pageable pageable) {
        return springDataJpaDepositRequestRepository.findByCustomerUuidAndBankNameAndStatus(customerUuid, depositorName, status, pageable);
    }


    @Override
    public Page<DepositRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable) {
        return springDataJpaDepositRequestRepository.findAllByCustomerUuid(customerUuid, pageable);
    }
}
