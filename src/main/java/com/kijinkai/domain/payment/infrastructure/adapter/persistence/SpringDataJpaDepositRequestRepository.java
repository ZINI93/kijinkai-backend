package com.kijinkai.domain.payment.infrastructure.adapter.persistence;

import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaDepositRequestRepository extends JpaRepository<DepositRequest, Long>{

    Optional<DepositRequest> findByRequestUuid(UUID reqeustUuid);

    List<DepositRequest> findByStatus(DepositStatus status);

    Optional<DepositRequest> findByRequestUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);

    @Query("SELECT dr FROM DepositRequest dr WHERE dr.customerUuid = :customerUuid AND dr.status = :status AND (:depositorName IS NULL OR dr.depositorName = :depositorName)")
    Page<DepositRequest> findByCustomerUuidAndBankNameAndStatus(@Param("customerUuid") UUID customerUuid, @Param("depositorName") String depositorName, @Param("status")DepositStatus status, Pageable pageable);


    Page<DepositRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);

}
