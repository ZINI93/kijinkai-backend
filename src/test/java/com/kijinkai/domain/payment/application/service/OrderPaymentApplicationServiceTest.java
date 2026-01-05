package com.kijinkai.domain.payment.application.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentDeliveryRequestDto;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;
import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
import com.kijinkai.domain.payment.application.port.out.OrderPaymentPersistencePort;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.model.OrderPayment;

import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserRole;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.application.mapper.WalletMapper;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.application.service.WalletApplicationService;
import com.kijinkai.domain.wallet.domain.model.Wallet;
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

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderPaymentApplicationServiceTest {

    @Mock CustomerPersistencePort customerPersistencePort;
    @Mock WalletPersistencePort walletPersistencePort;
    @Mock OrderItemPersistencePort orderItemPersistencePort;
    @Mock UserPersistencePort userPersistencePort;
    @Mock WalletMapper walletMapper;
    @Mock OrderPaymentApplicationService orderPaymentService;
    @Mock OrderPaymentPersistencePort orderPaymentPersistencePort;
    @Mock WalletApplicationService walletApplicationService;
    @Mock PaymentMapper paymentMapper;
    @Mock PaymentFactory paymentFactory;

    @InjectMocks OrderPaymentApplicationService orderPaymentApplicationService;

    UUID walletUuid;

    User user;
    Customer customer;
    Wallet wallet;
    Order order;
    OrderItem orderItem;
    OrderPayment orderPayment;

    WalletResponseDto walletResponseDto;
    OrderPaymentResponseDto orderPaymentResponseDto;

    OrderPaymentRequestDto orderPaymentRequestDto;

    BigDecimal totalPrice ;


    @BeforeEach
    void setUp() {

        totalPrice = new BigDecimal("100000");

        walletUuid = UUID.randomUUID();

        user = User.builder().userUuid(UUID.randomUUID()).build();
        customer = Customer.builder().userUuid(user.getUserUuid()).customerUuid(UUID.randomUUID()).build();
        wallet = Wallet.builder().customerUuid(customer.getCustomerUuid()).walletUuid(walletUuid).balance(totalPrice).build();
        order = Order.builder().customerUuid(customer.getCustomerUuid()).orderUuid(UUID.randomUUID()).build();
        orderItem = OrderItem.builder().customerUuid(customer.getCustomerUuid()).orderItemUuid(UUID.randomUUID()).priceOriginal(totalPrice).build();

        List<UUID> orderItemUuids = List.of(orderItem.getOrderItemUuid());

        orderPaymentRequestDto = OrderPaymentRequestDto.builder().deliveryFee(new BigDecimal("10000")).orderItemUuids(orderItemUuids).build();

        orderPayment = OrderPayment.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .paymentUuid(UUID.randomUUID())
                .orderPaymentStatus(OrderPaymentStatus.PENDING)
                .paymentType(PaymentType.PRODUCT_PAYMENT)
                .paymentAmount(totalPrice)
                .orderUuid(order.getOrderUuid())
                .build();

        orderPaymentResponseDto = OrderPaymentResponseDto.builder()
                .customerUuid(orderPayment.getCustomerUuid())
                .walletUuid(orderPayment.getWalletUuid())
                .paymentUuid(orderPayment.getPaymentUuid())
                .build();
    }

//    @Test
//    void completeFirstPayment() {
//        //given
//        List<OrderItem> orderItems = List.of(orderItem);
//        List<UUID> orderItemUuids = List.of(orderItem.getOrderItemUuid());
//
//        OrderPaymentRequestDto orderPaymentRequestDto = OrderPaymentRequestDto.builder().orderItemUuids(orderItemUuids).build();
//
//        WalletResponseDto walletResponseDto = WalletResponseDto.builder().customerUuid(customer.getCustomerUuid()).walletUuid(wallet.getWalletUuid()).balance(totalPrice).build();
//
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
//        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));
//        when(paymentFactory.createOrderFirstPayment(customer, wallet)).thenReturn(orderPayment);
//        when(orderPaymentPersistencePort.saveOrderPayment(any(OrderPayment.class))).thenReturn(orderPayment);
//        when(orderItemPersistencePort.firstOrderItemPayment(customer.getCustomerUuid(), orderPaymentRequestDto, orderPayment.getPaymentUuid())).thenReturn(orderItems);
//        when(walletApplicationService.withdrawal(customer.getCustomerUuid(), wallet.getWalletUuid(), totalPrice)).thenReturn(walletResponseDto);
//        when(paymentMapper.completeOrderPayment(orderPayment, walletResponseDto)).thenReturn(orderPaymentResponseDto);
//
//        //when
//        OrderPaymentResponseDto result = orderPaymentApplicationService.completeFirstPayment(user.getUserUuid(), orderPaymentRequestDto);
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getPaymentUuid()).isEqualTo(orderPayment.getPaymentUuid());
//    }

    @Test
    @DisplayName("관리자 - 두번째 결제 생성, 배송비에 대한 결제")
    void createSecondPayment() {
        //given

        User admin = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();

        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(orderItemPersistencePort.findByOrderItemUuid(orderItem.getOrderItemUuid())).thenReturn(Optional.of(orderItem));
        when(customerPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(customer));
        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));
        when(paymentFactory.createOrderSecondPayment(customer, orderPaymentRequestDto.getDeliveryFee() , wallet, admin.getUserUuid())).thenReturn(orderPayment);
        when(orderPaymentPersistencePort.saveOrderPayment(any(OrderPayment.class))).thenReturn(orderPayment);
        when(paymentMapper.createOrderPayment(orderPayment)).thenReturn(orderPaymentResponseDto);

        //when
        OrderPaymentResponseDto result = orderPaymentApplicationService.createSecondPayment(admin.getUserUuid(), orderPaymentRequestDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getPaymentUuid()).isEqualTo(orderPayment.getPaymentUuid());
    }

    @Test
    void completeSecondPayment() {
        //given

        List<UUID> paymentUuids = List.of(orderPayment.getPaymentUuid());
        WalletResponseDto walletResponseDto = WalletResponseDto.builder().customerUuid(customer.getCustomerUuid()).walletUuid(wallet.getWalletUuid()).balance(totalPrice).build();
        OrderPaymentDeliveryRequestDto requestDto = OrderPaymentDeliveryRequestDto.builder().orderPaymentUuids(paymentUuids).build();
        List<OrderPayment> orderPayments = List.of(orderPayment);


        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(orderPaymentPersistencePort.findByPaymentUuidInAndCustomerUuid(requestDto.getOrderPaymentUuids(),customer.getCustomerUuid())).thenReturn(List.of(orderPayment));
        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));
        when(walletApplicationService.withdrawal(customer.getCustomerUuid(), wallet.getWalletUuid(), totalPrice)).thenReturn(walletResponseDto);
        when(orderPaymentPersistencePort.saveAll(orderPayments)).thenReturn(List.of(orderPayment));
        when(paymentMapper.completeOrderPayment(orderPayment, walletResponseDto)).thenReturn(orderPaymentResponseDto);

        //when
        OrderPaymentResponseDto result = orderPaymentApplicationService.completeSecondPayment(user.getUserUuid(), requestDto);

        //then
        assertThat(result).isNotNull();

    }

    @Test
    void getOrderPaymentInfoByAdmin() {
        //given
        User admin = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();

        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(orderPaymentPersistencePort.findByPaymentUuid(orderPayment.getPaymentUuid())).thenReturn(Optional.of(orderPayment));
        when(paymentMapper.orderPaymentInfo(orderPayment)).thenReturn(orderPaymentResponseDto);

        //when
        OrderPaymentResponseDto result = orderPaymentApplicationService.getOrderPaymentInfoByAdmin(admin.getUserUuid(), orderPayment.getPaymentUuid());

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void getOrderPaymentInfo() {
        //given
        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(orderPaymentPersistencePort.findByCustomerUuidAndPaymentUuid(customer.getCustomerUuid(),orderPayment.getPaymentUuid())).thenReturn(Optional.of(orderPayment));
        when(paymentMapper.orderPaymentInfo(orderPayment)).thenReturn(orderPaymentResponseDto);

        //when
        OrderPaymentResponseDto result = orderPaymentApplicationService.getOrderPaymentInfo(user.getUserUuid(), orderPayment.getPaymentUuid());

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void getOrderPaymentsByStatusAndType() {
        //given

        PageRequest pageable = PageRequest.of(0, 10);
        List<OrderPayment> mockData = List.of(orderPayment);
        PageImpl<OrderPayment> mockPage = new PageImpl<>(mockData, pageable, mockData.size());

        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(orderPaymentPersistencePort.findAllByCustomerUuidAndOrderPaymentStatusAndPaymentTypeOrderByCreatedAtDesc(customer.getCustomerUuid(),OrderPaymentStatus.PENDING,PaymentType.PRODUCT_PAYMENT,pageable)).thenReturn(mockPage);
        when(paymentMapper.orderPaymentInfo(orderPayment)).thenReturn(orderPaymentResponseDto);

        //when
        Page<OrderPaymentResponseDto> result = orderPaymentApplicationService.getOrderPaymentsByStatusAndType(user.getUserUuid(), OrderPaymentStatus.PENDING, PaymentType.PRODUCT_PAYMENT, pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

    }


    @Test
    void getOrderPayments() {
        //given

        PageRequest pageable = PageRequest.of(0, 10);
        List<OrderPayment> mockData = List.of(orderPayment);
        PageImpl<OrderPayment> mockPage = new PageImpl<>(mockData, pageable, mockData.size());

        when(userPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(user));
        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(orderPaymentPersistencePort.findAllByCustomerUuid(customer.getCustomerUuid(), pageable)).thenReturn(mockPage);
        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));
        when(paymentMapper.orderPaymentDetailsInfo(orderPayment,wallet.getBalance())).thenReturn(orderPaymentResponseDto);

        //when
        Page<OrderPaymentResponseDto> result = orderPaymentApplicationService.getOrderPayments(user.getUserUuid(), pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getOrderPaymentDashboardCount() {
        //given

        //when

        //then
    }
}