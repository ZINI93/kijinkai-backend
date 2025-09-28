package com.kijinkai.domain.customer.application.service;


import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.address.factory.AddressFactory;
import com.kijinkai.domain.address.repository.AddressRepository;
import com.kijinkai.domain.customer.application.dto.CustomerCreateResponse;
import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.application.mapper.CustomerMapper;
import com.kijinkai.domain.customer.application.port.in.CreateCustomerUseCase;
import com.kijinkai.domain.customer.application.port.in.DeleteCustomerUseCase;
import com.kijinkai.domain.customer.application.port.in.GetCustomerUseCase;
import com.kijinkai.domain.customer.application.port.in.UpdateCustomerUseCase;
import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.application.validator.CustomerApplicationValidator;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.factory.CustomerFactory;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.customer.domain.model.CustomerTier;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


// exception, jpa 의존 변경 필요

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CustomerApplicationService implements CreateCustomerUseCase, GetCustomerUseCase, UpdateCustomerUseCase, DeleteCustomerUseCase {

    private final CustomerPersistencePort customerPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final AddressRepository addressRepository; // 포트 생성되면 변경


    private final CustomerFactory customerFactory;
    private final CustomerMapper customerMapper;
    private final CustomerApplicationValidator customerApplicationValidator;

    // 외부
    private final AddressFactory addressFactory;
    private final WalletService walletService;


    /**
     * 고객 정보 생성
     * * address 분리에 대해서 생각해 볼 필요 있음
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public CustomerCreateResponse createCustomer(UUID userUuid, CustomerRequestDto requestDto) {
        log.info("Creating customer for user uuid:{}", userUuid);

        // 1. request 검증
        customerApplicationValidator.validateCreateCustomerRequest(requestDto);


        // 2. 유저에 대한 검증
        User user = findUserByUserUuid(userUuid);
        user.validateRegistrationEligibility();

        // 3. 생성
        Customer customer = customerFactory.createCustomer(userUuid, requestDto);

        // 4. 저장
        Customer savedCustomer = customerPersistencePort.saveCustomer(customer);

        // 5. 주소 정보 생성
        Address address = addressFactory.createAddress(customer.getCustomerUuid(), requestDto);
        Address savedAddress = addressRepository.save(address);

        // 6. 지갑생성  -- 나중에 비동기로 전환 필요함
        walletService.createWalletWithValidate(customer);

        return customerMapper.createCustomerWithAddressResponse(savedCustomer, savedAddress, userUuid);

    }

    @Override
    public CustomerResponseDto getCustomerInfo(UUID userUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        return customerMapper.toResponse(customer);
    }

    @Override
    public Page<CustomerResponseDto> getCustomers(UUID userUuid, String firstName, String lastName, String phoneNumber, CustomerTier customerTier, Pageable pageable) {
        User user = findUserByUserUuid(userUuid);
        user.validateAdminRole();

        Page<Customer> customers = customerPersistencePort.findAllByCustomers(firstName, lastName, phoneNumber, customerTier, pageable);

        return customers.map(customerMapper::toResponse);
    }

    @Override
    @Transactional
    public CustomerResponseDto updateCustomer(UUID customerUuid, CustomerUpdateDto updateDto) {
        Customer customer = customerPersistencePort.findByCustomerUuid(customerUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for customerUuid: %s", customerUuid)));

        customer.updateCustomer(updateDto);
        Customer savedCustomer = customerPersistencePort.saveCustomer(customer);

        return customerMapper.toResponse(savedCustomer);
    }


    @Override
    @Transactional
    public void deleteCustomer(Customer customer) {
        // 현재 미구현
        // 회원 탈퇴한 유저들을 모아서 주기적으로 삭제하는 로직이 필요
    }
    // helper method

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", userUuid)));
    }

    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for useruuid: %s", userUuid)));
    }
}
