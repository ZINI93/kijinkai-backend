package com.kijinkai.domain.address.adapter.out.persistence.repository;

import com.kijinkai.domain.address.adapter.out.persistence.entity.AddressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AddressRepository extends JpaRepository<AddressJpaEntity, Long> {

    Optional<AddressJpaEntity> findByCustomerUuidAndAddressUuid(UUID customerUuid, UUID addressUuid);
    Optional<AddressJpaEntity> findByCustomerUuid(UUID customerUuid);
}