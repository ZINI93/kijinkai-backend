//package com.kijinkai.domain.order.service;
//
//import com.kijinkai.domain.customer.entity.Customer;
//import com.kijinkai.domain.customer.repository.CustomerRepository;
//import com.kijinkai.domain.order.dto.OrderRequestDto;
//import com.kijinkai.domain.order.dto.OrderResponseDto;
//import com.kijinkai.domain.order.dto.OrderUpdateDto;
//import com.kijinkai.domain.order.entity.Order;
//import com.kijinkai.domain.order.entity.OrderStatus;
//import com.kijinkai.domain.order.factory.OrderFactory;
//import com.kijinkai.domain.order.mapper.OrderMapper;
//import com.kijinkai.domain.order.repository.OrderRepository;
//import com.kijinkai.domain.order.validator.OrderValidator;
//import com.kijinkai.domain.orderitem.dto.OrderItemResponseDto;
//import com.kijinkai.domain.payment.entity.PaymentStatus;
//import com.kijinkai.domain.user.entity.User;
//import com.kijinkai.domain.user.entity.UserRole;
//import com.kijinkai.domain.user.repository.UserRepository;
//import com.kijinkai.domain.user.validator.UserValidator;
//import org.junit.jupiter.api.BeforeEach;
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
//import static org.mockito.ArgumentMatchers.any;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceImplTest {
//
//    @Mock OrderRepository orderRepository;
//    @Mock UserRepository userRepository;
//    @Mock CustomerRepository customerRepository;
//    @Mock OrderItemResponseDto orderItemResponseDto;
//    @Mock OrderFactory orderFactory;
//    @Mock OrderValidator orderValidator;
//    @Mock OrderMapper orderMapper;
//    @Mock UserValidator userValidator;
//    @InjectMocks OrderServiceImpl orderService;
//
//    User user;
//    Customer customer;
//    Order order;
//    OrderRequestDto requestDto;
//    OrderResponseDto responseDto;
//
//    @BeforeEach
//    void setUp(){
//
//        user = User.builder().userUuid(UUID.randomUUID().toString()).userRole(UserRole.ADMIN).build();
//        customer = Customer.builder().customerUuid(UUID.randomUUID().toString()).user(user).build();
//        requestDto = OrderRequestDto.builder().memo("memo").build();
//        order = new com.kijinkai.domain.order.entity.Order(
//                UUID.randomUUID().toString(),
//                customer,
//                new BigDecimal(1000.00),
//                new BigDecimal(1000.00),
//                new BigDecimal(1000.00),
//                "KOR",
//                OrderStatus.DRAFT,
//                requestDto.getMemo(),
//                null,
//                PaymentStatus.PENDING
//        );
//
//        responseDto = OrderResponseDto.builder().orderUuid(order.getOrderUuid()).build();
//
//
//    }
//
//    @Test
//    void createOrderProcess() {
//        //given
//        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
//        when(orderFactory.createOrder(customer,requestDto.getMemo())).thenReturn(order);
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//        when(orderMapper.toResponse(order)).thenReturn(responseDto);
//
//        //when
//        OrderResponseDto result = orderService.createOrderProcess(user.getUserUuid(), requestDto);
//
//        //then
//        assertNotNull(result);
//        assertEquals(order.getOrderUuid(),result.getOrderUuid());
//
//        verify(orderRepository,times(1)).save(any(Order.class));
//    }
//
//    @Test
//    void updateOrderWithValidate() {
//        //given
//
//        OrderUpdateDto updateOrder = OrderUpdateDto.builder().orderstate(OrderStatus.PENDING_APPROVAL).memo("good").build();
//
//        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
//        when(orderRepository.findByCustomerCustomerUuidAndOrderUuid(customer.getCustomerUuid(),order.getOrderUuid())).thenReturn(Optional.ofNullable(order));
//        when(orderMapper.toResponse(order)).thenReturn(responseDto);
//
//        //when
//        OrderResponseDto result = orderService.updateOrderWithValidate(customer.getUser().getUserUuid(), order.getOrderUuid(), updateOrder);
//
//        //then
//        assertNotNull(result);
//        assertEquals(order.getOrderUuid(),result.getOrderUuid());
//    }
//
//    @Test
//    void getOrderInfo() {
//        //given
//        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
//        when(orderRepository.findByCustomerCustomerUuidAndOrderUuid(customer.getCustomerUuid(),order.getOrderUuid())).thenReturn(Optional.ofNullable(order));
//        when(orderMapper.toResponse(order)).thenReturn(responseDto);
//
//
//        //when
//        OrderResponseDto result = orderService.getOrderInfo(customer.getUser().getUserUuid(), order.getOrderUuid());
//
//        //then
//        assertNotNull(result);
//        assertEquals(order.getOrderUuid(),result.getOrderUuid());
//    }
//
//    @Test
//    void cancelOrder() {
//
//        //given
//
//        OrderResponseDto cancelResponse = OrderResponseDto.builder().orderUuid(order.getOrderUuid()).orderstate(OrderStatus.CANCELLED).build();
//
//        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
//        when(orderRepository.findByCustomerCustomerUuidAndOrderUuid(customer.getCustomerUuid(),order.getOrderUuid())).thenReturn(Optional.ofNullable(order));
//        when(orderMapper.toResponse(order)).thenReturn(cancelResponse);
//
//        //when
//        OrderResponseDto result = orderService.cancelOrder(customer.getUser().getUserUuid(), order.getOrderUuid());
//
//        //then
//        assertNotNull(result);
//        assertEquals(order.getOrderUuid(),result.getOrderUuid());
//        assertEquals(OrderStatus.CANCELLED, result.getOrderstate());
//
//    }
//
//    @Test
//    void confirmOrder() {
//
//        //given
//
//        OrderResponseDto confirmResponse = OrderResponseDto.builder().orderUuid(order.getOrderUuid()).orderstate(OrderStatus.PREPARE_DELIVERY).build();
//
//        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
//        when(orderRepository.findByCustomerCustomerUuidAndOrderUuid(customer.getCustomerUuid(),order.getOrderUuid())).thenReturn(Optional.ofNullable(order));
//        when(orderMapper.toResponse(order)).thenReturn(confirmResponse);
//
//        //when
//        OrderResponseDto result = orderService.cancelOrder(customer.getUser().getUserUuid(), order.getOrderUuid());
//
//        //then
//        assertNotNull(result);
//        assertEquals(order.getOrderUuid(),result.getOrderUuid());
//        assertEquals(OrderStatus.PREPARE_DELIVERY, result.getOrderstate());
//    }
//
//    @Test
//    void deleteOrder() {
//        //given
//        when(customerRepository.findByUserUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(customer));
//        when(orderRepository.findByCustomerCustomerUuidAndOrderUuid(customer.getCustomerUuid(),order.getOrderUuid())).thenReturn(Optional.ofNullable(order));
//
//        //when
//        orderService.deleteOrder(customer.getUser().getUserUuid(),order.getOrderUuid());
//    }
//}