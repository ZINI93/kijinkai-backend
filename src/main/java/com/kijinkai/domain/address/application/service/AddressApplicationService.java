package com.kijinkai.domain.address.application.service;


import com.kijinkai.domain.address.application.dto.AddressRequestDto;
import com.kijinkai.domain.address.application.dto.AddressResponseDto;
import com.kijinkai.domain.address.application.dto.AddressUpdateDto;
import com.kijinkai.domain.address.application.mapper.AddressMapper;
import com.kijinkai.domain.address.application.port.in.CreateAddressUseCase;
import com.kijinkai.domain.address.application.port.in.DeleteAddressUseCase;
import com.kijinkai.domain.address.application.port.in.GetAddressUseCase;
import com.kijinkai.domain.address.application.port.in.UpdateAddressUseCase;
import com.kijinkai.domain.address.application.port.out.AddressPersistencePort;
import com.kijinkai.domain.address.domain.exception.AddressNotFoundException;
import com.kijinkai.domain.address.domain.factory.AddressFactory;
import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class AddressApplicationService implements CreateAddressUseCase, GetAddressUseCase, UpdateAddressUseCase, DeleteAddressUseCase {

    private final AddressPersistencePort addressPersistencePort;
    private final CustomerPersistencePort customerPersistencePort;

    private final AddressFactory addressFactory;
    private final AddressMapper addressMapper;

    /**
     * \
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public AddressResponseDto createAddress(UUID userUuid, AddressRequestDto requestDto) {

        try {
            Customer customer = findCustomerByUserUuid(userUuid);
            Address address = addressFactory.createAddress(customer.getCustomerUuid(), requestDto);
            Address savedAddress = addressPersistencePort.saveAddress(address);

            return addressMapper.toResponse(savedAddress);
        } catch (Exception e) {
            log.error("Address creation failed: userUUid={}, error={}", userUuid, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAddress(UUID userUuid, UUID addressUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Address address = addressPersistencePort.findByCustomerUuidAndAddressUuid(customer.getCustomerUuid(), addressUuid)
                .orElseThrow(() -> new AddressNotFoundException(String.format("Address not found for customerUuid: %s and addressUuid: %s", customer.getCustomerUuid(), addressUuid)));

        addressPersistencePort.deleteAddress(address);
    }


    /**
     * 유저 자신의 주소 조회
     *
     * @param userUuid
     * @return
     */
    @Override
    public AddressResponseDto getAddressInfo(UUID userUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Address address = findAddressByCustomerUuid(customer.getCustomerUuid());

        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(UUID userUuid, AddressUpdateDto updateDto) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Address address = findAddressByCustomerUuid(customer.getCustomerUuid());
        address.updateAddress(updateDto);
        Address savedAddress = addressPersistencePort.saveAddress(address);

        return addressMapper.toResponse(savedAddress);
    }

    // helper method
    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", userUuid)));
    }

    private Address findAddressByCustomerUuid(UUID customerUuid) {
        return addressPersistencePort.findByCustomerUuid(customerUuid)
                .orElseThrow(() -> new AddressNotFoundException(String.format("Address not found for customerUuid: %s", customerUuid)));
    }
}
