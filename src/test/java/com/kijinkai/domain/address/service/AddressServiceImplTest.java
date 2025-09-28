//package com.kijinkai.domain.address.service;
//
//import com.kijinkai.domain.address.dto.AddressRequestDto;
//import com.kijinkai.domain.address.dto.AddressResponseDto;
//import com.kijinkai.domain.address.dto.AddressUpdateDto;
//import com.kijinkai.domain.address.entity.Address;
//import com.kijinkai.domain.address.factory.AddressFactory;
//import com.kijinkai.domain.address.mapper.AddressMapper;
//import com.kijinkai.domain.address.repository.AddressRepository;
//import com.kijinkai.domain.customer.adapter.out.persistence.entity.Customer;
//import com.kijinkai.domain.customer.adapter.out.persistence.repository.CustomerRepository;
//import com.kijinkai.domain.user.domain.model.UserRole;
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
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class AddressServiceImplTest {
//
//    @Mock
//    AddressRepository addressRepository;
//
//    @Mock
//    CustomerRepository customerRepository;
//
//    @Mock
//    AddressFactory addressFactory;
//
//    @Mock
//    AddressMapper addressMapper;
//
//    @InjectMocks
//    AddressServiceImpl addressService;
//
//    UUID userUuid;
//    UUID customerUuid;
//    UUID addressUuid;
//
//    @BeforeEach
//    void setUp() {
//
//        userUuid = UUID.randomUUID();
//        customerUuid = UUID.randomUUID();
//        addressUuid = UUID.randomUUID();
//
//    }
//
//    private User createMockUser(UUID userUuid, UserRole userRole) {
//        return User.builder().userUuid(userUuid).userRole(userRole).build();
//    }
//
//    private Customer createMockCustomer(User user) {
//        return Customer.builder().user(user).build();
//    }
//
//    private Address createMockAddress(UUID addressUuid, Customer customer, AddressRequestDto requestDto) {
//        return Address.builder().addressUuid(addressUuid).customer(customer).zipcode(requestDto.getZipcode()).build();
//    }
//
//    @Test
//    void createAddressWithValidate() {
//
//        //Given
//
//        AddressRequestDto request = AddressRequestDto.builder().zipcode("123-123").build();
//
//        User user = createMockUser(userUuid, UserRole.USER);
//        Customer customer = createMockCustomer(user);
//        Address address = createMockAddress(addressUuid, customer, request);
//        AddressResponseDto response = AddressResponseDto.builder().customerUuid(customerUuid).addressUuid(addressUuid).zipcode(address.getZipcode()).build();
//
//
//        when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.ofNullable(customer));
//        when(addressFactory.createAddress(customer,request)).thenReturn(address);
//        when(addressRepository.save(any(Address.class))).thenReturn(address);
//        when(addressMapper.toResponse(address)).thenReturn(response);
//
//        //When
//        AddressResponseDto result = addressService.createAddressWithValidate(userUuid, request);
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getZipcode()).isEqualTo("123-123");
//
//
//        verify(customerRepository,times(1)).findByUserUuid(userUuid);
//        verify(addressRepository,times(1)).save(any(Address.class));
//
//        verify(addressMapper).toResponse(address);
//    }
//
//    @Test
//    @DisplayName("주소 업데이트")
//    void updateAddressWithValidate() {
//
//        //Given
//        AddressUpdateDto updateDto = AddressUpdateDto.builder().zipcode("333-333").build();
//
//        User user = createMockUser(userUuid, UserRole.USER);
//        Customer customer = createMockCustomer(user);
//        Address address = Address.builder().addressUuid(addressUuid).customer(customer).zipcode("123-123").build();
//        AddressResponseDto response = AddressResponseDto.builder().customerUuid(customerUuid).addressUuid(addressUuid).zipcode(updateDto.getZipcode()).build();
//
//        when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.ofNullable(customer));
//        when(addressRepository.findByCustomerUuidAndAddressUuid(customer.getCustomerUuid(),addressUuid)).thenReturn(Optional.ofNullable(address));
//        when(addressMapper.toResponse(address)).thenReturn(response);
//
//        //When
//        AddressResponseDto result = addressService.updateAddressWithValidate(userUuid, addressUuid, updateDto);
//
//        //Then
//        assertThat(result).isNotNull();
//        assertThat(result.getZipcode()).isEqualTo("333-333");
//
//
//        verify(customerRepository,times(1)).findByUserUuid(userUuid);
//        verify(addressRepository,times(1)).findByCustomerUuidAndAddressUuid(customer.getCustomerUuid(),addressUuid);
//    }
//
//    @Test
//    @DisplayName("주소 정보 조회")
//    void getAddressInfo() {
//
//        //Given
//        User user = createMockUser(userUuid, UserRole.USER);
//        Customer customer = createMockCustomer(user);
//        Address address = Address.builder().addressUuid(addressUuid).customer(customer).zipcode("123-123").build();
//        AddressResponseDto response = AddressResponseDto.builder().customerUuid(customerUuid).addressUuid(addressUuid).zipcode(address.getZipcode()).build();
//
//        when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.ofNullable(customer));
//        when(addressRepository.findByCustomerUuidAndAddressUuid(customer.getCustomerUuid(),addressUuid)).thenReturn(Optional.ofNullable(address));
//        when(addressMapper.toResponse(address)).thenReturn(response);
//
//
//        //When
//        AddressResponseDto result = addressService.getAddressInfo(userUuid, addressUuid);
//
//        //Then
//        assertThat(result).isNotNull();
//        assertThat(result.getZipcode()).isEqualTo("123-123");
//
//        verify(customerRepository,times(1)).findByUserUuid(userUuid);
//        verify(addressRepository,times(1)).findByCustomerUuidAndAddressUuid(customer.getCustomerUuid(),addressUuid);
//    }
//
//    @Test
//    @DisplayName("주소 삭제")
//    void deleteAddress() {
//
//        //Given
//        User user = createMockUser(userUuid, UserRole.USER);
//        Customer customer = createMockCustomer(user);
//        Address address = Address.builder().addressUuid(addressUuid).customer(customer).zipcode("123-123").build();
//
//        when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.ofNullable(customer));
//        when(addressRepository.findByCustomerUuidAndAddressUuid(customer.getCustomerUuid(),addressUuid)).thenReturn(Optional.ofNullable(address));
//
//        //When
//        addressService.deleteAddress(userUuid, addressUuid);
//
//        //Then
//
//    }
//}