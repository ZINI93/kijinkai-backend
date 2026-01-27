package com.kijinkai.domain.payment.adapter.out.persistence.repository;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.DepositRequestJpaEntity;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaDepositRequestRepository extends JpaRepository<DepositRequestJpaEntity, Long>{

    Optional<DepositRequestJpaEntity> findByRequestUuid(UUID reqeustUuid);

    List<DepositRequestJpaEntity> findByStatus(DepositStatus status);

    Optional<DepositRequestJpaEntity> findByRequestUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);

    @Query("SELECT dr FROM DepositRequestJpaEntity dr WHERE dr.customerUuid = :customerUuid AND dr.status = :status AND (:depositorName IS NULL OR dr.depositorName = :depositorName)")
    Page<DepositRequestJpaEntity> findByCustomerUuidAndBankNameAndStatus(@Param("customerUuid") UUID customerUuid, @Param("depositorName") String depositorName, @Param("status")DepositStatus status, Pageable pageable);

    @Query("SELECT dr FROM DepositRequestJpaEntity dr WHERE dr.status = :status ORDER BY createdAt desc")
    Page<DepositRequestJpaEntity> findAllByStatus(@Param("status") DepositStatus status, Pageable pageable);

    Page<DepositRequestJpaEntity> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);
}
