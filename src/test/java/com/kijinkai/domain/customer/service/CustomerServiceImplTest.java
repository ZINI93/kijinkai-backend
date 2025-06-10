package com.kijinkai.domain.customer.service;

import com.kijinkai.domain.customer.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.entity.CustomerTier;
import com.kijinkai.domain.customer.factory.CustomerFactory;
import com.kijinkai.domain.customer.mapper.CustomerMapper;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock CustomerRepository customerRepository;
    @Mock CustomerMapper mapper;
    @Mock CustomerFactory factory;
    @InjectMocks CustomerServiceImpl customerService;

    Customer customer;
    CustomerRequestDto requestDto;
    CustomerResponseDto responseDto;
    User user;



    @BeforeEach
    void setUp(){

        user = User.builder().userUuid(UUID.randomUUID().toString()).build();
        requestDto = new CustomerRequestDto("park", "jinhee", "010-1111-1111");
        customer = new Customer(UUID.randomUUID().toString(), requestDto.getFirstName(), requestDto.getLastName(), requestDto.getPhoneNumber(), CustomerTier.BRONZE, user);
        responseDto = new CustomerResponseDto(customer.getCustomerUuid(), customer.getFirstName(),customer.getLastName(),customer.getPhoneNumber(),customer.getCustomerTier(),customer.getUser().getUserUuid());

    }


    @Test
    void createCustomerWithValidate() {

        //given
        when(userRepository.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));
        when(factory.createCustomer(user,requestDto)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(mapper.toResponse(customer)).thenReturn(responseDto);

        //when
        CustomerResponseDto result = customerService.createCustomerWithValidate(user.getUserUuid(), requestDto);

        //then
        assertNotNull(result);
        assertEquals(customer.getUser().getUserUuid(),result.getUserUuid());
        assertEquals(customer.getFirstName(), result.getFirstName());

        verify(userRepository,times(1)).findByUserUuid(user.getUserUuid());
        verify(customerRepository,times(1)).save(any(Customer.class));
        verify(mapper,times(1)).toResponse(customer);

    }

    @Test
    void updateCustomerWithValidate() {

        //given
        CustomerUpdateDto customerUpdateDto = new CustomerUpdateDto("pa", "ku", "081-1111-1111");
        when(customerRepository.findByUserUserUuidAndCustomerUuid(user.getUserUuid(),customer.getCustomerUuid())).thenReturn(Optional.ofNullable(customer));
        CustomerResponseDto customerResponseDto = new CustomerResponseDto(customer.getCustomerUuid(), customerUpdateDto.getFirstName(), customerUpdateDto.getLastName(), customerUpdateDto.getPhoneNumber(), customer.getCustomerTier(), customer.getUser().getUserUuid());
        when(mapper.toResponse(customer)).thenReturn(customerResponseDto);

        //when
        CustomerResponseDto result = customerService.updateCustomerWithValidate(user.getUserUuid(), customer.getCustomerUuid(), customerUpdateDto);

        //then
        assertNotNull(result);
        assertEquals(customerUpdateDto.getFirstName(),result.getFirstName());
        assertEquals(customerUpdateDto.getPhoneNumber(),result.getPhoneNumber());

        verify(customerRepository,times(1)).findByUserUserUuidAndCustomerUuid(user.getUserUuid(),customer.getCustomerUuid());
        verify(mapper,times(1)).toResponse(customer);
    }

    @Test
    void getCustomerInfo() {
        //given
        when(customerRepository.findByUserUserUuidAndCustomerUuid(user.getUserUuid(),customer.getCustomerUuid())).thenReturn(Optional.ofNullable(customer));
        when(mapper.toResponse(customer)).thenReturn(responseDto);

        //when
        CustomerResponseDto result = customerService.getCustomerInfo(user.getUserUuid(), customer.getCustomerUuid());

        //then
        assertNotNull(result);
        assertEquals(customer.getCustomerUuid(),result.getCustomerUuid());
        assertEquals(customer.getCustomerTier(),result.getCustomerTier());

        verify(customerRepository,times(1)).findByUserUserUuidAndCustomerUuid(user.getUserUuid(),customer.getCustomerUuid());
        verify(mapper,times(1)).toResponse(customer);
    }
}