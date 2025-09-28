package com.kijinkai.domain.address.repository;

import com.kijinkai.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByCustomerUuidAndAddressUuid(UUID customerUuid, UUID addressUuid);
    Optional<Address> findByCustomerUuid(UUID customerUuid);
}