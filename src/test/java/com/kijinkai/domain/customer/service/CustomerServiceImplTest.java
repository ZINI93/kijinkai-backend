package com.kijinkai.domain.customer.service;

import com.kijinkai.domain.common.UuidValidator;
import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.entity.CustomerTier;
import com.kijinkai.domain.customer.factory.CustomerFactory;
import com.kijinkai.domain.customer.mapper.CustomerMapper;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.user.entity.User;

import com.kijinkai.domain.user.entity.UserStatus;
import com.kijinkai.domain.user.repository.UserRepository;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock CustomerRepository customerRepository;
    @Mock CustomerMapper customerMapper;
    @Mock CustomerFactory factory;
    @Mock WalletService walletService;
    @Mock UuidValidator uuidValidator;
    @InjectMocks CustomerServiceImpl customerService;

    Customer customer;
    CustomerRequestDto requestDto;
    CustomerResponseDto responseDto;
    WalletResponseDto walletResponseDto;
    User user;
    Wallet wallet;
    String customerUuid;




    @BeforeEach
    void setUp(){

        customerUuid = UUID.randomUUID().toString();

        wallet = Wallet.builder().walletUuid(UUID.randomUUID()).build();
        walletResponseDto = WalletResponseDto.builder().build();

        user = User.builder().userUuid(UUID.randomUUID()).emailVerified(true).userStatus(UserStatus.ACTIVE).build();
        requestDto = new CustomerRequestDto("park", "jinhee", "010-1111-1111");
        customer = new Customer(UUID.fromString(customerUuid), requestDto.getFirstName(), requestDto.getLastName(), requestDto.getPhoneNumber(), CustomerTier.BRONZE, user);
        responseDto = new CustomerResponseDto(customer.getCustomerUuid(), customer.getFirstName(),customer.getLastName(),customer.getPhoneNumber(),customer.getCustomerTier(),customer.getUser().getUserUuid());

    }


    @Test
    void createCustomerWithValidate() {

        //given
        when(userRepository.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));
        when(walletService.createWalletWithValidate(customer)).thenReturn(walletResponseDto);
        when(factory.createCustomer(user,requestDto)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(responseDto);

        //when
        CustomerResponseDto result = customerService.createCustomerWithValidate(user.getUserUuid(), requestDto);

        //then
        assertNotNull(result);
        assertEquals(customer.getUser().getUserUuid(),result.getUserUuid());
        assertEquals(customer.getFirstName(), result.getFirstName());

        verify(userRepository,times(1)).findByUserUuid(user.getUserUuid());
        verify(customerRepository,times(1)).save(any(Customer.class));
        verify(customerMapper,times(1)).toResponse(customer);

    }

    @Test
    void updateCustomerWithValidate() {

        //given
        when(uuidValidator.parseUuid(customerUuid)).thenReturn(UUID.fromString(customerUuid));

        CustomerUpdateDto customerUpdateDto = new CustomerUpdateDto("pa", "ku", "081-1111-1111");
        when(customerRepository.findByUserUserUuidAndCustomerUuid(user.getUserUuid(),customer.getCustomerUuid())).thenReturn(Optional.ofNullable(customer));
        CustomerResponseDto customerResponseDto = new CustomerResponseDto(customer.getCustomerUuid(), customerUpdateDto.getFirstName(), customerUpdateDto.getLastName(), customerUpdateDto.getPhoneNumber(), customer.getCustomerTier(), customer.getUser().getUserUuid());
        when(customerMapper.toResponse(customer)).thenReturn(customerResponseDto);

        //when
        CustomerResponseDto result = customerService.updateCustomerWithValidate(user.getUserUuid(), this.customerUuid, customerUpdateDto);

        //then
        assertNotNull(result);
        assertEquals(customerUpdateDto.getFirstName(),result.getFirstName());
        assertEquals(customerUpdateDto.getPhoneNumber(),result.getPhoneNumber());

        verify(customerRepository,times(1)).findByUserUserUuidAndCustomerUuid(user.getUserUuid(),customer.getCustomerUuid());
        verify(customerMapper,times(1)).toResponse(customer);
    }

    @Test
    void getCustomerInfo() {
        //given
        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
        when(customerMapper.toResponse(customer)).thenReturn(responseDto);

        //when
        CustomerResponseDto result = customerService.getCustomerInfo(user.getUserUuid());

        //then
        assertNotNull(result);
        assertEquals(customer.getCustomerUuid(),result.getCustomerUuid());
        assertEquals(customer.getCustomerTier(),result.getCustomerTier());

        verify(customerRepository,times(1)).findByUserUserUuid(user.getUserUuid());
        verify(customerMapper,times(1)).toResponse(customer);
    }

//    @Test
//    @DisplayName("구매자 리스트 조회")
//    void getCustomersTest(){
//
//        //given
//        PageRequest pageable = PageRequest.of(0, 10);
//        List<CustomerResponseDto> mockData=List.of(customerMapper.toResponse(customer));
//        PageImpl<CustomerResponseDto> mockPage = new PageImpl<>(mockData, pageable, mockData.size());
//
//        when(userRepository.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));
//        when(customerRepository.findAllByCustomers(user.getUserUuid(), customer.getFirstName(), customer.getLastName(), customer.getPhoneNumber(), customer.getCustomerTier(), pageable)).thenReturn(mockPage);
//
//        //when
//        Page<CustomerResponseDto> result = customerService.getAllByCustomers(user.getUserUuid(), customer.getFirstName(), customer.getLastName(), customer.getPhoneNumber(), CustomerTier.BRONZE, pageable);
//
//
//        //then
//        assertEquals(1,result.getTotalElements());
//        assertEquals(1,result.getContent().size());
//        verify(customerRepository, times(1)).findAllByCustomers(user.getUserUuid(), customer.getFirstName(), customer.getLastName(), customer.getPhoneNumber(), customer.getCustomerTier(), pageable);
//    }
}