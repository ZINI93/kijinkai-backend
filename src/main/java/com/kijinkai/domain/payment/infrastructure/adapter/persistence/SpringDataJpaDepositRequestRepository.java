package com.kijinkai.domain.payment.infrastructure.adapter.persistence;

import com.kijinkai.domain.payment.application.port.out.DepositRequestPort;
import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaDepositRequestRepository extends JpaRepository<DepositRequest, Long>{

    Optional<DepositRequest> findByRequestUuid(UUID reqeustUuid);

    List<DepositRequest> findByStatus(DepositStatus status);

    Optional<DepositRequest> findByRequestUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);
}
