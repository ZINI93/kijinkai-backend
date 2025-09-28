package com.kijinkai.domain.customer.adapter.out.persistence.repository;

import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface CustomerRepository extends JpaRepository<CustomerJpaEntity, Long>, CustomerRepositoryCustom {

    Optional<CustomerJpaEntity> findByUserUuidAndCustomerUuid(UUID userUuid, UUID customerUuid);
    Optional<CustomerJpaEntity> findByUserUuid(UUID userUuid);
    Optional<CustomerJpaEntity> findByCustomerUuid(UUID customerUuid);
}