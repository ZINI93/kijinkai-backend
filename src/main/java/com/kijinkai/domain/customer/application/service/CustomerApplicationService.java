package com.kijinkai.domain.customer.application.service;


import com.kijinkai.domain.address.application.port.out.AddressPersistencePort;
import com.kijinkai.domain.address.domain.factory.AddressFactory;
import com.kijinkai.domain.address.domain.model.Address;
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
import com.kijinkai.domain.wallet.application.service.WalletApplicationService;
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

    private final CustomerFactory customerFactory;
    private final CustomerMapper customerMapper;
    private final CustomerApplicationValidator customerApplicationValidator;

    // 외부
    private final AddressPersistencePort addressPersistencePort;
    private final AddressFactory addressFactory;
    private final WalletApplicationService walletApplicationService;

    /**
     * 고객 정보 생성
     * @param userUuid
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @return
     */
    @Override
    @Transactional
    public Customer createCustomer(UUID userUuid, String firstName, String lastName, String phoneNumber) {
        log.info("Creating customer for user uuid:{}", userUuid);

        try {
            // 1. request 검증
            customerApplicationValidator.validateCreateCustomerRequest(firstName, lastName, phoneNumber);

            // 2. 유저에 대한 검증
            User user = findUserByUserUuid(userUuid);
            user.validateActive();

            // 3. 생성
            Customer customer = customerFactory.createCustomer(userUuid, firstName, lastName, phoneNumber);

            // 4. 저장
            return customerPersistencePort.saveCustomer(customer);
        } catch (Exception e) {
            log.error("Customer creation failed: userUuid={}, error={}", userUuid, e.getMessage(), e);
            throw e;
        }
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
    public CustomerResponseDto updateCustomer(UUID userUuid, CustomerUpdateDto updateDto) {
        Customer customer = customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", updateDto)));

        customer.updateCustomer(updateDto);
        Customer savedCustomer = customerPersistencePort.saveCustomer(customer);

        return customerMapper.toResponse(savedCustomer);
    }


    @Override
    @Transactional
    public void deleteCustomer(Customer customer) {
        // 현재 미구현
        // 회원 탈퇴한 유저들의 고객정보를 모아서 주기적으로 삭제하는 로직이 필요
    }
    // helper method

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", userUuid)));
    }

    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for userUuid: %s", userUuid)));
    }

}
