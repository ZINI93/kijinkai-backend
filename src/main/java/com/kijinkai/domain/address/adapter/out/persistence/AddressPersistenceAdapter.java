package com.kijinkai.domain.address.adapter.out.persistence;


import com.kijinkai.domain.address.adapter.out.persistence.entity.AddressJpaEntity;
import com.kijinkai.domain.address.adapter.out.persistence.mapper.AddressPersistenceMapper;
import com.kijinkai.domain.address.adapter.out.persistence.repository.AddressRepository;
import com.kijinkai.domain.address.application.port.out.AddressPersistencePort;
import com.kijinkai.domain.address.domain.model.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class AddressPersistenceAdapter implements AddressPersistencePort {

    private final AddressRepository addressRepository;
    private final AddressPersistenceMapper addressPersistenceMapper;


    @Override
    public Address saveAddress(Address address) {
        AddressJpaEntity addressJpaEntity = addressPersistenceMapper.toAddressJpaEntity(address);
        addressJpaEntity = addressRepository.save(addressJpaEntity);
        return addressPersistenceMapper.toAddress(addressJpaEntity);
    }

    @Override
    public Optional<Address> findByCustomerUuidAndAddressUuid(UUID customerUuid, UUID addressUuid) {
        return addressRepository.findByCustomerUuidAndAddressUuid(customerUuid,addressUuid)
                .map(addressPersistenceMapper::toAddress);
    }

    @Override
    public Optional<Address> findByCustomerUuid(UUID customerUuid) {
        return addressRepository.findByCustomerUuid(customerUuid)
                .map(addressPersistenceMapper::toAddress);
    }

    @Override
    public Boolean existsByCustomerUuid(UUID customerUuid) {
        return addressRepository.existsByCustomerUuid(customerUuid);
    }

    @Override
    public void deleteAddress(Address address) {
        AddressJpaEntity addressJpaEntity = addressPersistenceMapper.toAddressJpaEntity(address);
        addressRepository.delete(addressJpaEntity);
    }
}
