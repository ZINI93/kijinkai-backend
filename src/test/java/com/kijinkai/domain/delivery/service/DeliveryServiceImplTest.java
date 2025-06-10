package com.kijinkai.domain.delivery.service;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.entity.Delivery;
import com.kijinkai.domain.delivery.factory.DeliveryFactory;
import com.kijinkai.domain.delivery.mapper.DeliveryMapper;
import com.kijinkai.domain.delivery.repository.DeliveryRepository;
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
class DeliveryServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock CustomerRepository customerRepository;
    @Mock DeliveryRepository deliveryRepository;
    @Mock DeliveryMapper mapper;
    @Mock DeliveryFactory deliveryFactory;
    @InjectMocks DeliveryServiceImpl deliveryService;
    User user;
    Customer customer;
    Delivery delivery;
    DeliveryRequestDto requestDto;
    DeliveryResponseDto responseDto;

    @BeforeEach
    void setUp(){

        user = User.builder().userUuid(UUID.randomUUID().toString()).build();
        customer = Customer.builder().user(user).customerUuid(UUID.randomUUID().toString()).build();
        requestDto = new DeliveryRequestDto("jinhee park", "111-1111", "kanagawa","miyamae","hayaku");

        delivery = Delivery.builder().
                customer(customer).
                deliveryUuid(UUID.randomUUID().toString()).
                receiverName(requestDto.getReceiverName()).
                postalCode(requestDto.getPostalCode()).
                address1(requestDto.getAddress1()).
                address2(requestDto.getAddress2()).
                memo(requestDto.getMemo()).build();

        responseDto = new DeliveryResponseDto(delivery.getDeliveryUuid(),delivery.getCustomer().getCustomerUuid(),delivery.getReceiverName(),delivery.getPostalCode(), delivery.getAddress1(), delivery.getAddress2(),delivery.getMemo());

    }

    @Test
    void createDeliveryWithValidate() {

        //given
        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(deliveryFactory.createDelivery(customer,requestDto)).thenReturn(delivery);
        when(mapper.toResponse(delivery)).thenReturn(responseDto);

        //when
        DeliveryResponseDto result = deliveryService.createDeliveryWithValidate(user.getUserUuid(), requestDto);

        //then
        assertNotNull(result);
        assertEquals(delivery.getDeliveryUuid(),result.getDeliveryUuid());
        assertEquals(delivery.getPostalCode(),result.getPostalCode());

        verify(customerRepository,times(1)).findByUserUserUuid(user.getUserUuid());
        verify(deliveryRepository,times(1)).save(any(Delivery.class));
        verify(mapper,times(1)).toResponse(delivery);

    }

    @Test
    void updateDeliveryWithValidate() {
        //given
        DeliveryUpdateDto updateDto = new DeliveryUpdateDto("nare", "123-123", "ibaraki", "kamikawa", "slow");
        DeliveryResponseDto updateResponse = DeliveryResponseDto.builder().address1(updateDto.getAddress1()).receiverName(updateDto.getReceiverName()).build();
        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
        when(deliveryRepository.findByCustomerCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(),delivery.getDeliveryUuid())).thenReturn(Optional.ofNullable(delivery));
        when(mapper.toResponse(delivery)).thenReturn(updateResponse);

        //when
        DeliveryResponseDto result = deliveryService.updateDeliveryWithValidate(user.getUserUuid(), delivery.getDeliveryUuid(), updateDto);


        //then
        assertNotNull(result);
        assertEquals(updateDto.getAddress1(), result.getAddress1());
        assertEquals(updateDto.getReceiverName(),result.getReceiverName());

        verify(customerRepository,times(1)).findByUserUserUuid(user.getUserUuid());
        verify(deliveryRepository,times(1)).findByCustomerCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(),delivery.getDeliveryUuid());
        verify(mapper,times(1)).toResponse(delivery);

    }

    @Test
    void deleteDelivery() {

        //given
        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
        when(deliveryRepository.findByCustomerCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(),delivery.getDeliveryUuid())).thenReturn(Optional.ofNullable(delivery));

        //when
        deliveryService.deleteDelivery(user.getUserUuid(),delivery.getDeliveryUuid());

    }

    @Test
    void getDeliveryInfo() {
        //given
        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
        when(deliveryRepository.findByCustomerCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(),delivery.getDeliveryUuid())).thenReturn(Optional.ofNullable(delivery));
        when(mapper.toResponse(delivery)).thenReturn(responseDto);

        //when
        DeliveryResponseDto result = deliveryService.getDeliveryInfo(user.getUserUuid(), delivery.getDeliveryUuid());

        //then
        assertNotNull(result);
        assertEquals(delivery.getReceiverName(),result.getReceiverName());
        assertEquals(delivery.getAddress1(),result.getAddress1());

        verify(customerRepository,times(1)).findByUserUserUuid(user.getUserUuid());
        verify(deliveryRepository,times(1)).findByCustomerCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(),delivery.getDeliveryUuid());
        verify(mapper,times(1)).toResponse(delivery);
    }
}