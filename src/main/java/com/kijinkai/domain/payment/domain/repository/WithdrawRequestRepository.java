package com.kijinkai.domain.payment.domain.repository;

import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WithdrawRequestRepository {

    Optional<WithdrawRequest> findByRequestUuid(UUID requestUuid);
    Optional<WithdrawRequest> findByRequestUuidAndUserUuid(UUID requestUuid, UUID userUUid);
    WithdrawRequest save(WithdrawRequest request);
}

