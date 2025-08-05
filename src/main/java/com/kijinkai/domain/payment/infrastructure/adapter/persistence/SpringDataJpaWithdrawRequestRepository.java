package com.kijinkai.domain.payment.infrastructure.adapter.persistence;


import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaWithdrawRequestRepository extends JpaRepository<WithdrawRequest, Long> {
    Optional<WithdrawRequest> findByRequestUuid(UUID requestUuid);
    Optional<WithdrawRequest> findByRequestUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);
}
