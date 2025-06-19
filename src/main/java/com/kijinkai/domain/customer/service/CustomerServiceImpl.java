package com.kijinkai.domain.customer.service;


import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.factory.CustomerFactory;
import com.kijinkai.domain.customer.mapper.CustomerMapper;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.exception.UserNotFoundException;
import com.kijinkai.domain.user.repository.UserRepository;
import com.kijinkai.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CustomerServiceImpl implements CustomerService{

    private final WalletService walletService;

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;
    private final CustomerFactory factory;

    @Override @Transactional
    public CustomerResponseDto createCustomerWithValidate(String userUuid, CustomerRequestDto requestDto) {

        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException("UserUuid : User not found exception"));
        Customer customer = factory.createCustomer(user, requestDto);
        Customer savedCustomer = customerRepository.save(customer);

        walletService.createWalletWithValidate(customer);

        return mapper.toResponse(savedCustomer);
    }

    private void updateCustomer(Customer customer, CustomerUpdateDto updateDto){
        customer.updateCustomer(updateDto.getFirstName(), updateDto.getLastName(), updateDto.getPhoneNumber());
    }


    @Override @Transactional
    public CustomerResponseDto updateCustomerWithValidate(String userUuid, String customerUuid, CustomerUpdateDto updateDto) {

        Customer customer = findCustomerByUserUuidAndCustomerUuid(userUuid, customerUuid);

        updateCustomer(customer,updateDto);

        return mapper.toResponse(customer);
    }

    @Override @Transactional
    public CustomerResponseDto getCustomerInfo(String userUuid, String customerUuid) {
        Customer customer = findCustomerByUserUuidAndCustomerUuid(userUuid, customerUuid);
        return mapper.toResponse(customer);
    }

    private Customer findCustomerByUserUuidAndCustomerUuid(String userUuid, String customerUuid) {
        return customerRepository.findByUserUserUuidAndCustomerUuid(userUuid, customerUuid)
                .orElseThrow(() -> new CustomerNotFoundException("UserUuid and CustomerUuid: Customer Not found"));
    }
}
