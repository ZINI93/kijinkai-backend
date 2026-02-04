//package com.kijinkai.domain.address.application.service;
//
//import com.kijinkai.domain.address.application.dto.AddressRequestDto;
//import com.kijinkai.domain.address.application.dto.AddressResponseDto;
//import com.kijinkai.domain.address.application.dto.AddressUpdateDto;
//import com.kijinkai.domain.address.application.mapper.AddressMapper;
//import com.kijinkai.domain.address.application.port.out.AddressPersistencePort;
//import com.kijinkai.domain.address.domain.factory.AddressFactory;
//import com.kijinkai.domain.address.domain.model.Address;
//import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
//import com.kijinkai.domain.customer.domain.model.Customer;
//import com.kijinkai.domain.user.domain.model.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class AddressApplicationServiceTest {
//
//    @Mock AddressPersistencePort addressPersistencePort;
//    @Mock CustomerPersistencePort customerPersistencePort;
//    @Mock AddressFactory addressFactory;
//    @Mock AddressMapper addressMapper;
//    @InjectMocks AddressApplicationService addressApplicationService;
//
//    AddressRequestDto requestDto;
//    AddressResponseDto responseDto;
//    Address address;
//    User user;
//    Customer customer;
//
//
//    @BeforeEach
//    void setUp(){
//
//        user = User.builder().userUuid(UUID.randomUUID()).build();
//        customer = Customer.builder().customerUuid(UUID.randomUUID()).userUuid(user.getUserUuid()).build();
//
//        requestDto = AddressRequestDto.builder()
//                .recipientName("kakaopark")
//                .recipientPhoneNumber("070-1111-1111")
//                .country("korea")
//                .zipcode("1111-1111")
//                .state("대구광역시")
//                .city("동구")
//                .street("신암동")
//                .build();
//
//
//        address = Address.builder()
//                .addressUuid(UUID.randomUUID())
//                .customerUuid(customer.getCustomerUuid())
//                .recipientName(requestDto.getRecipientName())
//                .recipientPhoneNumber(requestDto.getRecipientPhoneNumber())
//                .country(requestDto.getCountry())
//                .zipcode(requestDto.getZipcode())
//                .state(requestDto.getState())
//                .city(requestDto.getCity())
//                .street(requestDto.getStreet())
//                .isDefault(Boolean.TRUE)
//                .build();
//
//        responseDto = AddressResponseDto.builder()
//                .recipientName("kakaopark")
//                .recipientPhoneNumber("070-1111-1111")
//                .country("korea")
//                .zipcode("1111-1111")
//                .state("대구광역시")
//                .city("동구")
//                .street("신암동")
//                .build();
//    }
//
//    @Test
//    @DisplayName("주소 생성")
//    void createAddress() {
//
//        //given
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
//        when(addressFactory.createAddress(customer.getCustomerUuid(), requestDto)).thenReturn(address);
//        when(addressPersistencePort.saveAddress(any(Address.class))).thenReturn(address);
//        when(addressMapper.toResponse(address)).thenReturn(responseDto);
//
//        //when
//        AddressResponseDto result = addressApplicationService.createAddress(user.getUserUuid(), requestDto);
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getCity()).isEqualTo(address.getCity());
//        assertThat(result.getRecipientName()).isEqualTo(address.getRecipientName());
//
//        verify(customerPersistencePort,times(1)).findByUserUuid(user.getUserUuid());
//        verify(addressPersistencePort,times(1)).saveAddress(address);
//
//    }
//
//    @Test
//    @DisplayName("주소 삭제")
//    void deleteAddress() {
//
//        //given
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
//        when(addressPersistencePort.findByCustomerUuidAndAddressUuid(customer.getCustomerUuid(), address.getAddressUuid())).thenReturn(Optional.ofNullable(address));
//
//        //when
//
//        //then
//        addressApplicationService.deleteAddress(user.getUserUuid(), address.getAddressUuid());
//    }
//
//    @Test
//    @DisplayName("주소 조회")
//    void getAddressInfo() {
//        //given
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
//        when(addressPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.ofNullable(address));
//        when(addressMapper.toResponse(address)).thenReturn(responseDto);
//
//        //when
//        AddressResponseDto result = addressApplicationService.getAddressInfo(user.getUserUuid());
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getCity()).isEqualTo(address.getCity());
//        assertThat(result.getRecipientName()).isEqualTo(address.getRecipientName());
//
//        verify(customerPersistencePort,times(1)).findByUserUuid(user.getUserUuid());
//        verify(addressPersistencePort,times(1)).findByCustomerUuid(customer.getCustomerUuid());
//    }
//
//    @Test
//    @DisplayName("주소 업데이트")
//    void updateAddress() {
//
//        //given
//        AddressUpdateDto updateDto = AddressUpdateDto.builder()
//                .recipientName("kakaopark")
//                .recipientPhoneNumber("070-1111-1111")
//                .country("korea123")
//                .zipcode("2222-2222")
//                .state("남구")
//                .city("서울특별시")
//                .street("월하동")
//                .build();
//
//
//        AddressResponseDto updateResponse = AddressResponseDto.builder()
//                .recipientName(updateDto.getRecipientName())
//                .recipientPhoneNumber(updateDto.getRecipientPhoneNumber())
//                .country(updateDto.getCountry())
//                .zipcode(updateDto.getZipcode())
//                .state(updateDto.getState())
//                .city(updateDto.getCity())
//                .street(updateDto.getStreet())
//                .build();
//
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
//        when(addressPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.ofNullable(address));
//        when(addressPersistencePort.saveAddress(any(Address.class))).thenReturn(address);
//        when(addressMapper.toResponse(address)).thenReturn(updateResponse);
//
//        //when
//        AddressResponseDto result = addressApplicationService.updateAddress(user.getUserUuid(), updateDto);
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getCity()).isEqualTo("서울특별시");
//        assertThat(result.getRecipientName()).isEqualTo("kakaopark");
//
//        verify(customerPersistencePort,times(1)).findByUserUuid(user.getUserUuid());
//        verify(addressPersistencePort,times(1)).findByCustomerUuid(customer.getCustomerUuid());
//    }
//}