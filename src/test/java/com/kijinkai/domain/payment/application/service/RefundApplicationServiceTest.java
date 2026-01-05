package com.kijinkai.domain.payment.application.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
import com.kijinkai.domain.payment.application.dto.response.RefundResponseDto;
import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
import com.kijinkai.domain.payment.application.port.out.RefundPersistencePort;
import com.kijinkai.domain.payment.domain.enums.RefundStatus;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.model.RefundRequest;

import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserRole;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.application.service.WalletApplicationService;
import com.kijinkai.domain.wallet.domain.model.Wallet;

import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RefundApplicationServiceTest {

    @Mock
    OrderItemPersistencePort orderItemPersistencePort;
    @Mock
    CustomerPersistencePort customerPersistencePort;
    @Mock
    WalletPersistencePort walletPersistencePort;
    @Mock
    UserPersistencePort userPersistencePort;
    @Mock
    WalletApplicationService walletApplicationService;
    @Mock
    RefundPersistencePort refundPersistencePort;

    @Mock
    PaymentMapper paymentMapper;

    @Mock
    PaymentFactory paymentFactory;

    @InjectMocks
    RefundApplicationService refundApplicationService;

    User user;
    Customer customer;
    Wallet wallet;
    OrderItem orderItem;

    RefundRequest refundRequest;
    RefundRequestDto requestDto;
    RefundResponseDto responseDto;


    @BeforeEach
    void setUp() {

        user = User.builder().userUuid(UUID.randomUUID()).build();
        customer = Customer.builder().userUuid(user.getUserUuid()).customerUuid(UUID.randomUUID()).build();
        wallet = Wallet.builder().walletUuid(UUID.randomUUID()).customerUuid(customer.getCustomerUuid()).build();
        orderItem = OrderItem.builder().customerUuid(customer.getCustomerUuid()).orderItemUuid(UUID.randomUUID()).build();

        requestDto = RefundRequestDto.builder()
                .build();

        refundRequest = RefundRequest.builder()
                .refundUuid(UUID.randomUUID())
                .orderItemUuid(orderItem.getOrderItemUuid())
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .status(RefundStatus.PROCESSING)
                .refundAmount(new BigDecimal("100000"))
                .build();

        responseDto = RefundResponseDto.builder()
                .refundUuid(refundRequest.getRefundUuid())
                .customerUuid(refundRequest.getCustomerUuid())
                .walletUuid(refundRequest.getWalletUuid())
                .orderItemUuid(refundRequest.getOrderItemUuid())
                .status(refundRequest.getStatus())
                .refundAmount(refundRequest.getRefundAmount())
                .build();
    }


    @Test
    void processRefundRequest() {
        //given
        UUID adminUuid = UUID.randomUUID();

        when(orderItemPersistencePort.findByOrderItemUuid(orderItem.getOrderItemUuid())).thenReturn(Optional.of(orderItem));
        when(customerPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(customer));
        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));
        when(paymentFactory.createRefundPayment(customer, wallet, orderItem, orderItem.getPriceOriginal(), adminUuid, requestDto.getRefundReason(), requestDto.getRefundType()))
                .thenReturn(refundRequest);
        when(refundPersistencePort.save(any(RefundRequest.class))).thenReturn(refundRequest);
        when(paymentMapper.createRefundResponse(refundRequest)).thenReturn(responseDto);

        //when
        RefundResponseDto result = refundApplicationService.processRefundRequest(adminUuid, orderItem.getOrderItemUuid(), requestDto);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void approveRefundRequest() {
        //given
        User admin = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();
        WalletResponseDto walletResponseDto = WalletResponseDto.builder().walletUuid(wallet.getWalletUuid()).customerUuid(customer.getCustomerUuid()).build();

        BigDecimal refundAmount = new BigDecimal("100000");
        String memo = "아쉽지만 환불";

        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(refundPersistencePort.findByRefundUuid(refundRequest.getRefundUuid())).thenReturn(Optional.of(refundRequest));
        when(walletApplicationService.deposit(customer.getCustomerUuid(), wallet.getWalletUuid(), refundAmount)).thenReturn(walletResponseDto);
        when(refundPersistencePort.save(any(RefundRequest.class))).thenReturn(refundRequest);
        when(paymentMapper.processRefundResponse(refundRequest, walletResponseDto)).thenReturn(responseDto);

        //when
        RefundResponseDto result = refundApplicationService.approveRefundRequest(refundRequest.getRefundUuid(), admin.getUserUuid(), memo);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void getRefundInfoByAdmin() {

        //given
        User admin = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();
        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(refundPersistencePort.findByRefundUuid(refundRequest.getRefundUuid())).thenReturn(Optional.of(refundRequest));
        when(paymentMapper.refundInfoResponse(refundRequest)).thenReturn(responseDto);

        //when
        RefundResponseDto result = refundApplicationService.getRefundInfoByAdmin(refundRequest.getRefundUuid(), admin.getUserUuid());

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void getRefundInfo() {

        //given
        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(refundPersistencePort.findByRefundUuidAndCustomerUuid(refundRequest.getRefundUuid(), customer.getCustomerUuid())).thenReturn(Optional.of(refundRequest));
        when(paymentMapper.refundInfoResponse(refundRequest)).thenReturn(responseDto);

        //when
        RefundResponseDto result = refundApplicationService.getRefundInfo(refundRequest.getRefundUuid(), user.getUserUuid());
        //then
        assertThat(result).isNotNull();

    }

    @Test
    void getRefunds() {
        //given

        PageRequest pageable = PageRequest.of(0, 10);
        List<RefundRequest> mockData = List.of(refundRequest);
        PageImpl<RefundRequest> mockPage = new PageImpl<>(mockData, pageable, mockData.size());

        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));
        when(refundPersistencePort.findAllByCustomerUuid(customer.getCustomerUuid(), pageable)).thenReturn(mockPage);
        when(paymentMapper.refundDetailsInfo(refundRequest, wallet.getBalance())).thenReturn(responseDto);

        //when
        Page<RefundResponseDto> result = refundApplicationService.getRefunds(user.getUserUuid(), pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
    }
}