//package com.kijinkai.domain.delivery.service;
//
//import com.kijinkai.domain.address.adapter.out.persistence.entity.AddressJpaEntity;
//import com.kijinkai.domain.address.adapter.out.persistence.repository.AddressRepository;
//import com.kijinkai.domain.customer.adapter.out.persistence.entity.Customer;
//import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
//import com.kijinkai.domain.customer.adapter.out.persistence.repository.CustomerRepository;
//import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
//import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
//import com.kijinkai.domain.delivery.dto.DeliveryUpdateDto;
//import com.kijinkai.domain.delivery.entity.Carrier;
//import com.kijinkai.domain.delivery.entity.DeliveryJpaEntity;
//import com.kijinkai.domain.delivery.entity.DeliveryStatus;
//import com.kijinkai.domain.delivery.exception.DeliveryCreationException;
//import com.kijinkai.domain.delivery.exception.DeliveryNotFoundException;
//import com.kijinkai.domain.delivery.exception.DeliveryUpdateException;
//import com.kijinkai.domain.delivery.factory.DeliveryFactory;
//import com.kijinkai.domain.delivery.mapper.DeliveryMapper;
//import com.kijinkai.domain.delivery.repository.DeliveryRepository;
//import com.kijinkai.domain.delivery.validator.DeliveryValidator;
//import com.kijinkai.domain.order.entity.Order;
//import com.kijinkai.domain.order.entity.OrderStatus;
//import com.kijinkai.domain.order.repository.OrderRepository;
//import com.kijinkai.domain.order.validator.OrderValidator;
//import com.kijinkai.domain.user.domain.model.UserRole;
//import com.kijinkai.domain.user.adapter.in.web.validator.UserValidator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("DeliveryServiceImpl 테스트")
//class DeliveryServiceImplTest {
//
//    @Mock
//    private CustomerRepository customerRepository;
//    @Mock
//    private DeliveryRepository deliveryRepository;
//    @Mock
//    private OrderRepository orderRepository;
//    @Mock
//    private AddressRepository addressRepository;
//    @Mock
//    private DeliveryFactory factory;
//    @Mock
//    private DeliveryMapper deliveryMapper;
//    @Mock
//    private DeliveryValidator deliveryValidator;
//    @Mock
//    private UserValidator userValidator;
//    @Mock
//    private OrderValidator orderValidator;
//
//    @InjectMocks
//    private DeliveryServiceImpl deliveryService;
//
//    private Customer customer;
//    private User user;
//    private Order order;
//    private DeliveryJpaEntity delivery;
//    private AddressJpaEntity address;
//    private DeliveryRequestDto requestDto;
//    private DeliveryUpdateDto updateDto;
//    private DeliveryResponseDto responseDto;
//
//    @BeforeEach
//    void setUp() {
//        // Test 데이터 초기화
//        user = User.builder()
//                .userUuid(UUID.randomUUID())
//                .userRole(UserRole.USER)
//                .build();
//
//        customer = Customer.builder()
//                .customerUuid(UUID.randomUUID())
//                .user(user)
//                .build();
//
//        order = Order.builder()
//                .orderUuid(UUID.randomUUID())
//                .customer(customer)
//                .orderStatus(OrderStatus.FIRST_PAID)
//                .build();
//
//        delivery = DeliveryJpaEntity.builder()
//                .deliveryUuid(UUID.randomUUID())
//                .order(order)
//                .deliveryStatus(DeliveryStatus.PENDING)
//                .build();
//
//        address = AddressJpaEntity.builder()
//                .addressUuid(UUID.randomUUID())
//                .customer(customer)
//                .recipientName("홍길동")
//                .recipientPhoneNumber("010-1234-5678")
//                .country("대한민국")
//                .zipcode("12345")
//                .state("서울특별시")
//                .city("강남구")
//                .street("테헤란로 123")
//                .build();
//
//        requestDto = DeliveryRequestDto.builder()
//                .carrier(Carrier.YAMATO)
//                .trackingNumber("1234567890")
//                .deliveryFee(new BigDecimal(3000.00))
//                .build();
//
//        updateDto = DeliveryUpdateDto.builder()
//                .recipientName("김철수")
//                .recipientPhoneNumber("010-9876-5432")
//                .country("대한민국")
//                .zipcode("54321")
//                .state("부산광역시")
//                .city("해운대구")
//                .street("해운대로 456")
//                .carrier(Carrier.YAMATO)
//                .trackingNumber("0987654321")
//                .deliveryFee(new BigDecimal(2000.00))
//                .build();
//
//        responseDto = DeliveryResponseDto.builder()
//                .deliveryUuid(delivery.getDeliveryUuid())
//                .recipientName("홍길동")
//                .deliveryStatus(DeliveryStatus.PENDING)
//                .build();
//    }
//
//    @Test
//    @DisplayName("배송 생성 성공 - 유효한 주문으로 배송 생성")
//    void createDeliveryWithValidate_Success() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID orderUuid = order.getOrderUuid();
//
//        given(orderRepository.findByOrderUuid(orderUuid)).willReturn(Optional.of(order));
//        given(addressRepository.findByCustomerUuid(customer.getCustomerUuid())).willReturn(Optional.of(address));
//        given(factory.createDelivery(order, address, requestDto)).willReturn(delivery);
//        given(deliveryRepository.save(any(DeliveryJpaEntity.class))).willReturn(delivery);
//        given(deliveryMapper.toResponse(delivery)).willReturn(responseDto);
//
//        // when
//        DeliveryResponseDto result = deliveryService.createDeliveryWithValidate(userUuid, orderUuid, requestDto);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getDeliveryUuid()).isEqualTo(delivery.getDeliveryUuid());
//        assertThat(result.getRecipientName()).isEqualTo("홍길동");
//        assertThat(result.getDeliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
//
//        verify(orderValidator).requirePaidStatusForConfirmation(order);
//        verify(deliveryRepository).save(any(DeliveryJpaEntity.class));
//    }
//
//    @Test
//    @DisplayName("배송 생성 실패 - 존재하지 않는 주문")
//    void createDeliveryWithValidate_OrderNotFound() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID orderUuid = UUID.randomUUID();
//
//        given(orderRepository.findByOrderUuid(any(UUID.class)))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> deliveryService.createDeliveryWithValidate(userUuid, orderUuid, requestDto))
//                .isInstanceOf(DeliveryCreationException.class)
//                .hasMessage("Failed to create delivery");
//    }
//
//    @Test
//    @DisplayName("배송 생성 실패 - 주소 정보 없음")
//    void createDeliveryWithValidate_AddressNotFound() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID orderUuid = order.getOrderUuid();
//
//        given(orderRepository.findByOrderUuid(orderUuid))
//                .willReturn(Optional.of(order));
//        given(addressRepository.findByCustomerUuid(customer.getCustomerUuid()))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> deliveryService.createDeliveryWithValidate(userUuid, orderUuid, requestDto))
//                .isInstanceOf(DeliveryCreationException.class)
//                .hasMessage("Failed to create delivery");
//    }
//
//    @Test
//    @DisplayName("배송 시작 성공 - 관리자가 배송 시작")
//    void deliveryShipped_Success() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID deliveryUuid = delivery.getDeliveryUuid();
//        user.updateRole(UserRole.ADMIN);
//
//        given(customerRepository.findByUserUuid(userUuid)).willReturn(Optional.of(customer));
//        given(deliveryRepository.findByCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(), deliveryUuid)).willReturn(Optional.of(delivery));
//        given(deliveryMapper.toResponse(delivery)).willReturn(responseDto);
//
//        // when
//        DeliveryResponseDto result = deliveryService.deliveryShipped(userUuid, deliveryUuid);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getDeliveryUuid()).isEqualTo(delivery.getDeliveryUuid());
//
//        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.SHIPPING);
//        assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.SHIPPED);
//
//        verify(userValidator).requireAdminRole(user);
//        verify(deliveryValidator).requirePendingStatus(delivery);
//    }
//
//    @Test
//    @DisplayName("배송 시작 실패 - 고객 정보 없음")
//    void deliveryShipped_CustomerNotFound() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID deliveryUuid = delivery.getDeliveryUuid();
//
//        given(customerRepository.findByUserUuid(userUuid)).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> deliveryService.deliveryShipped(userUuid, deliveryUuid))
//                .isInstanceOf(CustomerNotFoundException.class)
//                .hasMessage("userUuid: customer not found");
//    }
//
//    @Test
//    @DisplayName("배송 정보 업데이트 성공 - 관리자가 배송 정보 수정")
//    void updateDeliveryWithValidate_Success() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID deliveryUuid = delivery.getDeliveryUuid();
//        user.updateRole(UserRole.ADMIN);
//        DeliveryResponseDto updateResponse = DeliveryResponseDto.builder().deliveryUuid(deliveryUuid).recipientPhoneNumber(updateDto.getRecipientPhoneNumber()).country(updateDto.getCountry()).build();
//
//        given(customerRepository.findByUserUuid(userUuid)).willReturn(Optional.of(customer));
//        given(deliveryRepository.findByCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(), deliveryUuid)).willReturn(Optional.of(delivery));
//        given(deliveryMapper.toResponse(delivery)).willReturn(updateResponse);
//
//        // when
//        DeliveryResponseDto result = deliveryService.updateDeliveryWithValidate(userUuid, deliveryUuid, updateDto);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getDeliveryUuid()).isEqualTo(delivery.getDeliveryUuid());
//        assertThat(result.getRecipientPhoneNumber()).isEqualTo("010-9876-5432");
//        assertThat(result.getCountry()).isEqualTo(updateDto.getCountry());
//
//        verify(userValidator).requireAdminRole(user);
//        verify(orderValidator).requirePaidStatusForConfirmation(order);
//    }
//
//    @Test
//    @DisplayName("배송 정보 업데이트 실패 - 업데이트 중 예외 발생")
//    void updateDeliveryWithValidate_UpdateException() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID deliveryUuid = delivery.getDeliveryUuid();
//        user.updateRole(UserRole.ADMIN);
//
//        given(customerRepository.findByUserUuid(userUuid)).willReturn(Optional.of(customer));
//        given(deliveryRepository.findByCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(), deliveryUuid)).willReturn(Optional.of(delivery));
//
//        doThrow(new RuntimeException("Order validation failed"))
//                .when(orderValidator).requirePaidStatusForConfirmation(any(Order.class));
//
//        // when & then
//        assertThatThrownBy(() -> deliveryService.updateDeliveryWithValidate(userUuid, deliveryUuid, updateDto))
//                .isInstanceOf(DeliveryUpdateException.class)
//                .hasMessage("Failed to update DeliveryJpaEntity");
//    }
//
//    @Test
//    @DisplayName("배송 삭제 성공 - 관리자가 배송 삭제")
//    void deleteDelivery_Success() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID deliveryUuid = delivery.getDeliveryUuid();
//        user.updateRole(UserRole.ADMIN);
//
//        given(customerRepository.findByUserUuid(userUuid))
//                .willReturn(Optional.of(customer));
//        given(deliveryRepository.findByCustomerUuidAndDeliveryUuid(
//                customer.getCustomerUuid(), deliveryUuid))
//                .willReturn(Optional.of(delivery));
//
//        // when
//        assertThatCode(() -> deliveryService.deleteDelivery(userUuid, deliveryUuid))
//                .doesNotThrowAnyException();
//
//        // then
//        verify(userValidator).requireAdminRole(user);
//        verify(deliveryRepository).delete(delivery);
//    }
//
//    @Test
//    @DisplayName("배송 삭제 실패 - 배송 정보 없음")
//    void deleteDelivery_DeliveryNotFound() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID deliveryUuid = delivery.getDeliveryUuid();
//        user.updateRole(UserRole.ADMIN);
//
//        given(customerRepository.findByUserUuid(userUuid))
//                .willReturn(Optional.of(customer));
//        given(deliveryRepository.findByCustomerUuidAndDeliveryUuid(
//                customer.getCustomerUuid(), deliveryUuid))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> deliveryService.deleteDelivery(userUuid, deliveryUuid))
//                .isInstanceOf(DeliveryNotFoundException.class)
//                .hasMessage("CustomerUuidAndDeliveryUuid: delivery not found");
//    }
//
//    @Test
//    @DisplayName("배송 정보 조회 성공 - 사용자가 배송 정보 확인")
//    void getDeliveryInfo_Success() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID deliveryUuid = delivery.getDeliveryUuid();
//
//        given(customerRepository.findByUserUuid(userUuid)).willReturn(Optional.of(customer));
//        given(deliveryRepository.findByCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(), deliveryUuid)).willReturn(Optional.of(delivery));
//        given(deliveryMapper.toResponse(delivery)).willReturn(responseDto);
//
//        // when
//        DeliveryResponseDto result = deliveryService.getDeliveryInfo(userUuid, deliveryUuid);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getDeliveryUuid()).isEqualTo(delivery.getDeliveryUuid());
//        assertThat(result.getRecipientName()).isEqualTo("홍길동");
//        assertThat(result.getDeliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
//
//        verify(customerRepository).findByUserUuid(userUuid);
//        verify(deliveryRepository).findByCustomerUuidAndDeliveryUuid(
//                customer.getCustomerUuid(), deliveryUuid);
//        verify(deliveryMapper).toResponse(delivery);
//    }
//
//    @Test
//    @DisplayName("배송 정보 조회 실패 - 잘못된 사용자 UUID")
//    void getDeliveryInfo_InvalidUserUuid() {
//        // given
//        String invalidUserUuid = "invalid-uuid";
//        UUID deliveryUuid = delivery.getDeliveryUuid();
//
//        // when & then
//        assertThatThrownBy(() -> deliveryService.getDeliveryInfo(UUID.fromString(invalidUserUuid), deliveryUuid))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    @DisplayName("배송 정보 조회 실패 - 잘못된 배송 UUID")
//    void getDeliveryInfo_InvalidDeliveryUuid() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        String invalidDeliveryUuid = "invalid-uuid";
//
//        // when & then
//        assertThatThrownBy(() -> deliveryService.getDeliveryInfo(userUuid, UUID.fromString(invalidDeliveryUuid)))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    @DisplayName("주문 조회 실패 - 존재하지 않는 주문 UUID")
//    void findOrderByOrderUuid_NotFound() {
//        // given
//        UUID orderUuid = UUID.randomUUID();
//        given(orderRepository.findByOrderUuid(orderUuid))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> deliveryService.createDeliveryWithValidate(
//                user.getUserUuid(), orderUuid, requestDto))
//                .isInstanceOf(DeliveryCreationException.class)
//                .hasMessage("Failed to create delivery");
//    }
//
//    @Test
//    @DisplayName("고객 조회 - 유효한 사용자 UUID로 고객 조회")
//    void findCustomerByUserUuid_Success() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID deliveryUuid = delivery.getDeliveryUuid();
//
//        given(customerRepository.findByUserUuid(userUuid))
//                .willReturn(Optional.of(customer));
//        given(deliveryRepository.findByCustomerUuidAndDeliveryUuid(
//                customer.getCustomerUuid(), deliveryUuid))
//                .willReturn(Optional.of(delivery));
//        given(deliveryMapper.toResponse(delivery))
//                .willReturn(responseDto);
//
//        // when
//        DeliveryResponseDto result = deliveryService.getDeliveryInfo(userUuid, deliveryUuid);
//
//        // then
//        assertThat(result).isNotNull();
//        verify(customerRepository).findByUserUuid(userUuid);
//    }
//
//    @Test
//    @DisplayName("고객 조회 실패 - 존재하지 않는 사용자 UUID")
//    void findCustomerByUserUuid_NotFound() {
//        // given
//        UUID userUuid = user.getUserUuid();
//        UUID deliveryUuid = delivery.getDeliveryUuid();
//
//        given(customerRepository.findByUserUuid(userUuid))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> deliveryService.getDeliveryInfo(userUuid, deliveryUuid))
//                .isInstanceOf(CustomerNotFoundException.class)
//                .hasMessage("userUuid: customer not found");
//    }
//}