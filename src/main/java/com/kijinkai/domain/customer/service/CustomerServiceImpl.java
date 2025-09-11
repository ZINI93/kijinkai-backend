package com.kijinkai.domain.customer.service;


import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.address.factory.AddressFactory;
import com.kijinkai.domain.address.repository.AddressRepository;
import com.kijinkai.domain.common.UuidValidator;
import com.kijinkai.domain.customer.dto.CustomerCreateResponse;
import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.entity.CustomerTier;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.factory.CustomerFactory;
import com.kijinkai.domain.customer.mapper.CustomerMapper;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.exception.UserNotFoundException;
import com.kijinkai.domain.user.repository.UserRepository;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CustomerServiceImpl implements CustomerService {

    private final WalletService walletService;


    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    private final CustomerMapper customerMapper;
    private final CustomerFactory customerFactory;
    private final AddressFactory addressFactory;

    private final UserValidator userValidator;
    private final UuidValidator uuidValidator;

    /**
     * 회원 가입 후 유저에서 고객으로 등록 후 정확한 배송정보를 얻기 위한 프로세스
     * 이때 계정의 지갑이 생김
     *
     * @param userUuid
     * @param requestDto
     * @return customerMapper
     */
    @Override
    @Transactional
    public CustomerCreateResponse createCustomerWithValidate(UUID userUuid, CustomerRequestDto requestDto) {

        User user = findUserByUserUuid(userUuid, "UserUuid : User not found exception");

//        if (!user.isEmailVerified()){
//            throw new EmailNotFoundException("이메일 인증이 필요 합니다.");
//        }

//        if (user.getUserStatus() != UserStatus.ACTIVE){
//            throw new InvalidUserStatusException("필수 정보를 모두 입력해 주세요.");
//        }

        Customer customer = customerFactory.createCustomer(user, requestDto);
        Customer savedCustomer = customerRepository.save(customer);

        Address address = addressFactory.createAddress(savedCustomer, requestDto);
        Address savedAddress = addressRepository.save(address);


        walletService.createWalletWithValidate(customer);
        return customerMapper.createCustomerWithAddressResponse(savedCustomer, savedAddress);
    }

    /**
     * 고객이 본인 계정을 업데이트 하는 프로세스
     *
     * @param userUuid
     * @param updateDto
     * @return CustomerMapper
     */
    @Override
    @Transactional
    public CustomerResponseDto updateCustomerWithValidate(UUID userUuid, CustomerUpdateDto updateDto) {

        Customer findBycustomer = findByUserUuid(userUuid);

        UUID customerUuid = uuidValidator.parseUuid(findBycustomer.getCustomerUuid());

        Customer customer = findCustomerByUserUuidAndCustomerUuid(userUuid, customerUuid);
        customer.updateCustomer(updateDto);
        return customerMapper.toResponse(customer);
    }

    /**
     * 구매자의 정보 조회 프로세트
     *
     * @param userUuid
     * @return CustomerMapper
     */
    @Override
    public CustomerResponseDto getCustomerInfo(UUID userUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        return customerMapper.toResponse(customer);
    }


    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer uuid not found for user uuid: %s", userUuid)));
    }

    /**
     * 관리자가 고객의 성, 이름, 전화번호, 티어로 놔눠서 을 하기 위한 프로세스
     *
     * @param userUuid
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param customerTier
     * @param pageable
     * @return
     */

    @Override
    public Page<CustomerResponseDto> getAllByCustomers(UUID userUuid, String firstName, String lastName, String phoneNumber, CustomerTier customerTier, Pageable pageable) {

        User user = findUserByUserUuid(userUuid, String.format("User not found for user uuid: %s", userUuid));
        userValidator.requireAdminRole(user);

        return customerRepository.findAllByCustomers(userUuid, firstName, lastName, phoneNumber, customerTier, pageable);
    }

    @Override
    public Customer findByUserUuid(UUID userUuid) {

        Customer customer = customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid: %s", userUuid)));

        return customer;
    }

    @Override
    public Customer findByCustomerUuid(UUID customerUuid) {
        return customerRepository.findByCustomerUuid(customerUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for customer uuid: %s", customerUuid)));
    }

    private User findUserByUserUuid(UUID userUuid, String userUuid1) {
        return userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(userUuid1));
    }

    private Customer findCustomerByUserUuidAndCustomerUuid(UUID userUuid, UUID customerUuid) {
        return customerRepository.findByUserUserUuidAndCustomerUuid(userUuid, customerUuid)
                .orElseThrow(() -> new CustomerNotFoundException("UserUuid and CustomerUuid: Customer Not found"));
    }
}
