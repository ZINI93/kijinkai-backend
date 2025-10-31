package com.kijinkai.domain.payment.adapter.out.persistence.repository;


import com.kijinkai.domain.payment.adapter.out.persistence.entity.WithdrawRequestJpaEntity;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaWithdrawRequestRepository extends JpaRepository<WithdrawRequestJpaEntity, Long> {
    Optional<WithdrawRequestJpaEntity> findByRequestUuid(UUID requestUuid);

    Optional<WithdrawRequestJpaEntity> findByRequestUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);

    @Query("SELECT wr FROM WithdrawRequestJpaEntity wr WHERE wr.status = :status AND (:bankName IS NULL OR wr.bankName = :bankName)")
    Page<WithdrawRequestJpaEntity> findByWithdrawPaymentUuidByStatus(@Param("bankName") String bankName, @Param("status") WithdrawStatus status, Pageable pageable);

    Page<WithdrawRequestJpaEntity> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);


}





