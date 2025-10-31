package com.kijinkai.domain.delivery.application.service;

import com.kijinkai.domain.address.application.port.out.AddressPersistencePort;
import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.domain.model.Carrier;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.application.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.application.mapper.DeliveryMapper;
import com.kijinkai.domain.delivery.application.out.DeliveryPersistencePort;
import com.kijinkai.domain.delivery.application.validator.DeliveryValidator;
import com.kijinkai.domain.delivery.domain.factory.DeliveryFactory;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import com.kijinkai.domain.order.application.validator.OrderValidator;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.OrderPaymentJpaEntity;
import com.kijinkai.domain.payment.application.port.out.OrderPaymentPersistencePort;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserRole;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryApplicationServiceTest {

    @Mock DeliveryPersistencePort deliveryPersistencePort;
    @Mock DeliveryFactory factory;
    @Mock DeliveryMapper deliveryMapper;
    @Mock DeliveryValidator deliveryValidator;

    //외부
    @Mock CustomerPersistencePort customerPersistencePort;
    @Mock UserPersistencePort userPersistencePort;
    @Mock AddressPersistencePort addressPersistencePort;
    @Mock UserApplicationValidator userValidator;
    @Mock OrderValidator orderValidator;

    @Mock OrderPaymentPersistencePort orderPaymentPersistencePort;  // 수정필요

    @InjectMocks DeliveryApplicationService deliveryApplicationService;


    //user
    User user;
    Address address;
    OrderPaymentJpaEntity orderPayment;
    Customer customer;
    Delivery delivery;
    DeliveryRequestDto deliveryRequestDto;
    DeliveryResponseDto deliveryResponseDto;


    // admin

    User admin;
    Address addressAdmin;
    OrderPayment orderPaymentAdmin;
    Customer customerAdmin;
    Delivery deliveryAdmin;
    DeliveryRequestDto deliveryRequestDtoAdmin;
    DeliveryResponseDto deliveryResponseDtoAdmin;



    @BeforeEach
    void setUp() {

        // user
        user = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.USER).build();
        customer = Customer.builder().customerUuid(UUID.randomUUID()).userUuid(user.getUserUuid()).build();
        orderPayment = OrderPaymentJpaEntity.builder().customerUuid(customer.getCustomerUuid()).build();
        address = Address.builder().customerUuid(customer.getCustomerUuid()).addressUuid(UUID.randomUUID()).build();

        delivery = Delivery.builder()
                .deliveryUuid(UUID.randomUUID())
                .orderPaymentUuid(orderPayment.getPaymentUuid())
                .customerUuid(customer.getCustomerUuid())
                .deliveryStatus(DeliveryStatus.PENDING)
                .recipientName("JinheePark")
                .recipientPhoneNumber("080-1111-1111")
                .country("japan")
                .zipcode("123-123")
                .state("스기나미구")
                .city("도쿄도")
                .street("시라사키1-1-1")
                .carrier(Carrier.YAMATO)
                .trackingNumber("123123123")
                .deliveryFee(new BigDecimal(10000.00))
                .build();


        deliveryRequestDto = DeliveryRequestDto.builder()
                .carrier(Carrier.YAMATO)
                .trackingNumber("222-222")
                .deliveryRequest("피규어 포장 부탁해요")
                .build();

        deliveryResponseDto = DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .customerUuid(customer.getCustomerUuid())
                .deliveryStatus(DeliveryStatus.PENDING)
                .recipientName("JinheePark")
                .recipientPhoneNumber("080-1111-1111")
                .country("japan")
                .zipcode("123-123")
                .state("스기나미구")
                .city("도쿄도")
                .street("시라사키1-1-1")
                .carrier(Carrier.YAMATO)
                .trackingNumber("123123123")
                .deliveryFee(new BigDecimal(10000.00))
                .build();

        // admin
        admin = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();

        customerAdmin = Customer.builder().customerUuid(UUID.randomUUID()).userUuid(admin.getUserUuid()).build();
        orderPaymentAdmin = OrderPayment.builder().customerUuid(customerAdmin.getCustomerUuid()).build();
        addressAdmin = Address.builder().customerUuid(customerAdmin.getCustomerUuid()).addressUuid(UUID.randomUUID()).build();

        deliveryAdmin = Delivery.builder()
                .deliveryUuid(UUID.randomUUID())
                .orderPaymentUuid(orderPaymentAdmin.getPaymentUuid())
                .customerUuid(customerAdmin.getCustomerUuid())
                .deliveryStatus(DeliveryStatus.PENDING)
                .recipientName("JinheePark")
                .recipientPhoneNumber("080-1111-1111")
                .country("japan")
                .zipcode("123-123")
                .state("스기나미구")
                .city("도쿄도")
                .street("시라사키1-1-1")
                .carrier(Carrier.YAMATO)
                .trackingNumber("123123123")
                .deliveryFee(new BigDecimal(10000.00))
                .build();


        deliveryRequestDtoAdmin = DeliveryRequestDto.builder()
                .carrier(Carrier.YAMATO)
                .trackingNumber("222-222")
                .deliveryRequest("피규어 포장 부탁해요")
                .build();

        deliveryResponseDtoAdmin = DeliveryResponseDto.builder()
                .deliveryUuid(deliveryAdmin.getDeliveryUuid())
                .customerUuid(customerAdmin.getCustomerUuid())
                .deliveryStatus(DeliveryStatus.PENDING)
                .recipientName("JinheePark")
                .recipientPhoneNumber("080-1111-1111")
                .country("japan")
                .zipcode("123-123")
                .state("스기나미구")
                .city("도쿄도")
                .street("시라사키1-1-1")
                .carrier(Carrier.YAMATO)
                .trackingNumber("123123123")
                .deliveryFee(new BigDecimal(10000.00))
                .build();

    }

    @Test
    @DisplayName("관리자 - 배송 생성")
    void createDelivery() {

        //given
        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(orderPaymentPersistencePort.findByPaymentUuid(orderPaymentAdmin.getPaymentUuid())).thenReturn(Optional.of(orderPaymentAdmin));
        when(customerPersistencePort.findByCustomerUuid(orderPaymentAdmin.getCustomerUuid())).thenReturn(Optional.of(customerAdmin));
        when(addressPersistencePort.findByCustomerUuid(customerAdmin.getCustomerUuid())).thenReturn(Optional.of(addressAdmin));
        when(factory.createDelivery(orderPaymentAdmin.getPaymentUuid(), customerAdmin.getCustomerUuid(), addressAdmin, orderPaymentAdmin.getPaymentAmount(), deliveryRequestDtoAdmin)).thenReturn(deliveryAdmin);
        when(deliveryPersistencePort.saveDelivery(any(Delivery.class))).thenReturn(deliveryAdmin);
        when(deliveryMapper.toResponse(deliveryAdmin)).thenReturn(deliveryResponseDtoAdmin);

        //when
        DeliveryResponseDto result = deliveryApplicationService.createDelivery(admin.getUserUuid(), orderPaymentAdmin.getPaymentUuid(), deliveryRequestDtoAdmin);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getCity()).isEqualTo(deliveryAdmin.getCity());
        assertThat(result.getState()).isEqualTo(deliveryAdmin.getState());

        verify(deliveryPersistencePort,times(1)).saveDelivery(deliveryAdmin);
    }

    @Test
    @DisplayName("관리자 - 배송 삭제")
    void deleteDelivery() {
        //given
        when(customerPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(customerAdmin));
        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(deliveryPersistencePort.findByCustomerUuidAndDeliveryUuid(customerAdmin.getCustomerUuid(),deliveryAdmin.getDeliveryUuid())).thenReturn(Optional.of(deliveryAdmin));

        //when
        deliveryApplicationService.deleteDelivery(admin.getUserUuid(),deliveryAdmin.getDeliveryUuid());

        //then
    }

    @Test
    @DisplayName("배송 시작") // status 변경 체크 필요함
    void deliveryShipped() {
        //given
        when(customerPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(customerAdmin));
        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(deliveryPersistencePort.findByCustomerUuidAndDeliveryUuid(customerAdmin.getCustomerUuid(),deliveryAdmin.getDeliveryUuid())).thenReturn(Optional.of(deliveryAdmin));
        when(deliveryMapper.toResponse(deliveryAdmin)).thenReturn(deliveryResponseDtoAdmin);

        //when
        DeliveryResponseDto result = deliveryApplicationService.shipDelivery(admin.getUserUuid(), deliveryAdmin.getDeliveryUuid());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getCity()).isEqualTo(deliveryAdmin.getCity());
        assertThat(result.getState()).isEqualTo(deliveryAdmin.getState());
    }

    @Test
    void getDeliveryInfo() {

        //given
        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(deliveryPersistencePort.findByCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(), delivery.getDeliveryUuid())).thenReturn(Optional.of(delivery));
        when(deliveryMapper.toResponse(delivery)).thenReturn(deliveryResponseDto);

        //when
        DeliveryResponseDto result = deliveryApplicationService.getDeliveryInfo(user.getUserUuid(), delivery.getDeliveryUuid());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getCity()).isEqualTo(delivery.getCity());
        assertThat(result.getState()).isEqualTo(delivery.getState());
    }

    @Test
    void getDeliveriesByStatus() {
        //given
        PageRequest pageable = PageRequest.of(0, 10);
        List<Delivery> mockData = List.of(delivery);
        PageImpl<Delivery> mockPage = new PageImpl<>(mockData, pageable, mockData.size());

        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(deliveryPersistencePort.findByCustomerUuidByStatus(customer.getCustomerUuid(), delivery.getDeliveryStatus(), pageable)).thenReturn(mockPage);
        when(deliveryMapper.searchResponse(delivery)).thenReturn(deliveryResponseDto);
        //when
        Page<DeliveryResponseDto> result = deliveryApplicationService.getDeliveriesByStatus(user.getUserUuid(), delivery.getDeliveryStatus(), pageable);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getState()).isEqualTo(delivery.getState());

    }

    @Test
    void getDeliveryDashboardCount() {

        //given
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
//        when(deliveryPersistencePort.findByDeliveryStatusCount(customer.getCustomerUuid(), DeliveryStatus.SHIPPED));
//        when(deliveryPersistencePort.findByDeliveryStatusCount(customer.getCustomerUuid(), DeliveryStatus.DELIVERED));
//        when(deliveryMapper.deliveryCount())

        //when
//        DeliveryCountResponseDto result = deliveryApplicationService.getDeliveryDashboardCount(user.getUserUuid());

        //then
    }

    @Test
    void updateDeliveryWithValidate() {
        //given

        DeliveryUpdateDto deliveryUpdateDto = DeliveryUpdateDto.builder()
                .recipientName("kokopark")
                .recipientPhoneNumber("070-1111-1111")
                .country("korea")
                .zipcode("123-123")
                .state("동구")
                .city("대구광역시")
                .street("신암동1-1-1")
                .carrier(Carrier.YAMATO)
                .trackingNumber("222222")
                .deliveryFee(new BigDecimal(20000.00))
                .build();

        DeliveryResponseDto updateResponse = DeliveryResponseDto.builder()
                .recipientName(deliveryUpdateDto.getRecipientName())
                .recipientPhoneNumber(deliveryUpdateDto.getRecipientPhoneNumber())
                .country(deliveryUpdateDto.getCountry())
                .zipcode(deliveryUpdateDto.getZipcode())
                .state(deliveryUpdateDto.getState())
                .city(deliveryUpdateDto.getCity())
                .street(deliveryUpdateDto.getStreet())
                .carrier(deliveryUpdateDto.getCarrier())
                .trackingNumber(deliveryUpdateDto.getTrackingNumber())
                .deliveryFee(deliveryUpdateDto.getDeliveryFee())
                .build();

        when(customerPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(customerAdmin));
        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(deliveryPersistencePort.findByCustomerUuidAndDeliveryUuid(customerAdmin.getCustomerUuid(),deliveryAdmin.getDeliveryUuid())).thenReturn(Optional.of(deliveryAdmin));
        when(deliveryMapper.toResponse(deliveryAdmin)).thenReturn(updateResponse);

        //when
        DeliveryResponseDto result = deliveryApplicationService.updateDeliveryWithValidate(admin.getUserUuid(), deliveryAdmin.getDeliveryUuid(), deliveryUpdateDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getCity()).isEqualTo(deliveryUpdateDto.getCity());
        assertThat(result.getState()).isEqualTo(deliveryUpdateDto.getState());
    }
}