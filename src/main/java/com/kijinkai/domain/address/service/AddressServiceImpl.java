package com.kijinkai.domain.address.service;

import com.kijinkai.domain.address.dto.AddressRequestDto;
import com.kijinkai.domain.address.dto.AddressResponseDto;
import com.kijinkai.domain.address.dto.AddressUpdateDto;
import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.address.exception.AddressNotFoundException;
import com.kijinkai.domain.address.factory.AddressFactory;
import com.kijinkai.domain.address.mapper.AddressMapper;
import com.kijinkai.domain.address.repository.AddressRepository;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    private final AddressFactory addressFactory;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressResponseDto createAddressWithValidate(UUID userUuid, AddressRequestDto requestDto) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Address address = addressFactory.createAddress1(customer, requestDto);
        Address savedAddress = addressRepository.save(address);

        return addressMapper.toResponse(savedAddress);
    }

    @Override @Transactional
    public AddressResponseDto updateAddressWithValidate(UUID userUuid, AddressUpdateDto updateDto) {

        Customer findByCustomer = findCustomerByUserUuid(userUuid);
        Address findByAddress = findAddressByCustomer(findByCustomer);

        Address address = findAddressByCustomerUuidAndAddressUuid(findByAddress.getAddressUuid(), findByCustomer);
        address.updateAddress(updateDto);
        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponseDto getAddressInfo(UUID userUuid) {
        Customer findBycustomer = findCustomerByUserUuid(userUuid);
        Address findByaddress = findAddressByCustomer(findBycustomer);

        Address address = findAddressByCustomerUuidAndAddressUuid(findByaddress.getAddressUuid(), findBycustomer);

        return addressMapper.toResponse(address);
    }

    private Address findAddressByCustomer(Customer findBycustomer) {
        return addressRepository.findByCustomerCustomerUuid(findBycustomer.getCustomerUuid())
                .orElseThrow(() -> new AddressNotFoundException(String.format("Address not found exception for customer uuid: %s", findBycustomer.getCustomerUuid())));
    }

    @Override
    public void deleteAddress(UUID userUuid, UUID addressUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Address address = findAddressByCustomerUuidAndAddressUuid(addressUuid, customer);

        addressRepository.delete(address);

    }

    private Address findAddressByCustomerUuidAndAddressUuid(UUID addressUuid, Customer customer) {
        return addressRepository.findByCustomerCustomerUuidAndAddressUuid(customer.getCustomerUuid(), addressUuid)
                .orElseThrow(() -> new AddressNotFoundException("CustomerUuid and AddressUuid : Address not found"));
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException("UserUuid: Customer not found"));
    }
}
