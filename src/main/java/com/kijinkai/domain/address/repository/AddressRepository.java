package com.kijinkai.domain.address.repository;

import com.kijinkai.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByCustomerCustomerUuidAndAddressUuid(String customerUuid, String addressUuid);
    Optional<Address> findByCustomerCustomerUuid(String customerUuid);
}