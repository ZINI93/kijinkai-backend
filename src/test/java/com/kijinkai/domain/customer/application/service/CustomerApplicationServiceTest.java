package com.kijinkai.domain.customer.application.service;

import com.kijinkai.domain.address.application.port.out.AddressPersistencePort;
import com.kijinkai.domain.address.domain.factory.AddressFactory;
import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.customer.application.dto.CustomerCreateResponse;
import com.kijinkai.domain.customer.application.dto.CustomerRequestDto;
import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.application.dto.CustomerUpdateDto;
import com.kijinkai.domain.customer.application.mapper.CustomerMapper;
import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.application.validator.CustomerApplicationValidator;
import com.kijinkai.domain.customer.domain.factory.CustomerFactory;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserRole;
import com.kijinkai.domain.user.domain.model.UserStatus;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerApplicationServiceTest {

    @Mock CustomerPersistencePort customerPersistencePort;
    @Mock UserPersistencePort userPersistencePort;
    @Mock CustomerFactory customerFactory;
    @Mock CustomerMapper customerMapper;
    @Mock CustomerApplicationValidator customerApplicationValidator;

    @Mock AddressFactory addressFactory;

    // 추후에 수정파일
    @Mock WalletService walletService;
    @Mock AddressPersistencePort addressPersistencePort;

    @InjectMocks CustomerApplicationService customerApplicationService;

    Customer customer;
    User user;
    Address address;
    CustomerRequestDto customerRequestDto;
    CustomerResponseDto customerResponseDto;

    @BeforeEach
    void setUp() {

        user = User.builder().userUuid(UUID.randomUUID()).userStatus(UserStatus.ACTIVE).build();
        address = Address.builder().addressUuid(UUID.randomUUID()).build();


        customerRequestDto = CustomerRequestDto.builder()
                .firstName("Park")
                .lastName("Jinhee")
                .phoneNumber("080-1111-1111")
                .build();

        customer = Customer.builder()
                .customerUuid(UUID.randomUUID())
                .userUuid(user.getUserUuid())
                .firstName(customerRequestDto.getFirstName())
                .lastName(customerRequestDto.getLastName())
                .phoneNumber(customerRequestDto.getPhoneNumber())
                .build();

        customerResponseDto = CustomerResponseDto
                .builder().
                customerUuid(customer.getCustomerUuid())
                .userUuid(customer.getUserUuid())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }

    @Test
    @DisplayName("고객 정보 생성(주소,지갑도 같이 생성한다)")
    void createCustomer() {

        //given
        CustomerCreateResponse customerCreateResponse = CustomerCreateResponse.builder()
                .customerUuid(customer.getCustomerUuid())
                .userUuid(user.getUserUuid())
                .phoneNumber(customer.getPhoneNumber())
                .addressUuid(address.getAddressUuid())
                .build();

        // 고객
        when(userPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));
        when(customerFactory.createCustomer(user.getUserUuid(), customerRequestDto)).thenReturn(customer);
        when(customerPersistencePort.saveCustomer(any(Customer.class))).thenReturn(customer);

        // 주소
        when(addressFactory.createAddressAndCustomer(customer.getCustomerUuid(), customerRequestDto)).thenReturn(address);
        when(addressPersistencePort.saveAddress(any(Address.class))).thenReturn(address);

        when(customerMapper.createCustomerWithAddressResponse(customer,address,user.getUserUuid())).thenReturn(customerCreateResponse);

        //when
        CustomerCreateResponse result = customerApplicationService.createCustomer(user.getUserUuid(), customerRequestDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getUserUuid()).isEqualTo(user.getUserUuid());
        assertThat(result.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
        assertThat(result.getAddressUuid()).isEqualTo(address.getAddressUuid());

        verify(userPersistencePort, times(1)).findByUserUuid(user.getUserUuid());
        verify(customerPersistencePort,times(1)).saveCustomer(customer);
        verify(customerApplicationValidator).validateCreateCustomerRequest(customerRequestDto);
    }

    @Test
    @DisplayName("유저 - 고객 본인의 정보 조회")
    void getCustomerInfo() {
        //given
        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponseDto);

        //when
        CustomerResponseDto result = customerApplicationService.getCustomerInfo(user.getUserUuid());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getUserUuid()).isEqualTo(user.getUserUuid());
        assertThat(result.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());

        verify(customerPersistencePort, times(1)).findByUserUuid(user.getUserUuid());
    }

    @Test
    void getCustomers() {
        //given
        User admin = User.builder()
                .userUuid(UUID.randomUUID())
                .userRole(UserRole.ADMIN)
                .userStatus(UserStatus.ACTIVE)
                .build();

        Customer customerAdmin = Customer.builder()
                .customerUuid(UUID.randomUUID())
                .userUuid(admin.getUserUuid())
                .firstName(customerRequestDto.getFirstName())
                .lastName(customerRequestDto.getLastName())
                .phoneNumber(customerRequestDto.getPhoneNumber())
                .build();

        CustomerResponseDto customerResponseDtoAdmin = CustomerResponseDto
                .builder().
                customerUuid(customer.getCustomerUuid())
                .userUuid(customer.getUserUuid())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .build();


        PageRequest pageable = PageRequest.of(0, 10);
        List<Customer> mockData = List.of(customerAdmin);
        PageImpl<Customer> mockPage = new PageImpl<>(mockData, pageable, mockData.size());

        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(customerPersistencePort.findAllByCustomers(customerAdmin.getFirstName(), customerAdmin.getLastName(), customerAdmin.getPhoneNumber(), customerAdmin.getCustomerTier(), pageable)).thenReturn(mockPage);
        when(customerMapper.toResponse(any(Customer.class))).thenReturn(customerResponseDtoAdmin);

        //when
        Page<CustomerResponseDto> result = customerApplicationService.getCustomers(admin.getUserUuid(), customerAdmin.getFirstName(), customerAdmin.getLastName(), customerAdmin.getPhoneNumber(), customerAdmin.getCustomerTier(), pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo(customer.getFirstName());

        verify(userPersistencePort, times(1)).findByUserUuid(admin.getUserUuid());

    }

    @Test
    @DisplayName("고객 정보 업데이트")
    void updateCustomer() {
        //given

        CustomerUpdateDto customerUpdateDto = CustomerUpdateDto.builder()
                .firstName("wakaoji")
                .firstName("xeiba")
                .phoneNumber("080-1111-1111")
                .build();

        CustomerResponseDto updateResponseDto = CustomerResponseDto
                .builder()
                .firstName(customerUpdateDto.getFirstName())
                .lastName(customerUpdateDto.getLastName())
                .phoneNumber(customerUpdateDto.getPhoneNumber())
                .build();

        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
        when(customerPersistencePort.saveCustomer(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(updateResponseDto);

        //when
        CustomerResponseDto result = customerApplicationService.updateCustomer(user.getUserUuid(), customerUpdateDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(customerUpdateDto.getFirstName());
        assertThat(result.getPhoneNumber()).isEqualTo(customerUpdateDto.getPhoneNumber());

        verify(customerPersistencePort, times(1)).findByUserUuid(user.getUserUuid());

    }

    @Test
    @DisplayName("미구현")
    void deleteCustomer() {
        //given

        //when

        //then

    }
}