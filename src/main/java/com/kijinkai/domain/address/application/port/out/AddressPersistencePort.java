package com.kijinkai.domain.address.application.port.out;


import com.kijinkai.domain.address.domain.model.Address;

import java.util.Optional;
import java.util.UUID;

public interface AddressPersistencePort {

    Optional<Address> findByCustomerUuidAndAddressUuid(UUID customerUuid, UUID addressUuid);

    Address saveAddress(Address address);
    Optional<Address> findByCustomerUuid(UUID customerUuid);

    void deleteAddress(Address address);
}
