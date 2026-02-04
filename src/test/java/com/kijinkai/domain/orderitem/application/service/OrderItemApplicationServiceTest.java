//package com.kijinkai.domain.orderitem.application.service;
//
//import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
//import com.kijinkai.domain.customer.domain.model.Customer;
//import com.kijinkai.domain.delivery.domain.model.Delivery;
//import com.kijinkai.domain.exchange.doamin.Currency;
//import com.kijinkai.domain.exchange.service.PriceCalculationService;
//import com.kijinkai.domain.order.application.validator.OrderValidator;
//import com.kijinkai.domain.order.domain.model.Order;
//import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
//import com.kijinkai.domain.orderitem.application.dto.OrderItemCountResponseDto;
//import com.kijinkai.domain.orderitem.application.dto.OrderItemRequestDto;
//import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
//import com.kijinkai.domain.orderitem.application.dto.OrderItemUpdateDto;
//import com.kijinkai.domain.orderitem.application.mapper.OrderItemMapper;
//import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
//import com.kijinkai.domain.orderitem.application.validator.OrderItemValidator;
//import com.kijinkai.domain.orderitem.domain.factory.OrderItemFactory;
//import com.kijinkai.domain.orderitem.domain.model.OrderItem;
//import com.kijinkai.domain.payment.adapter.out.persistence.entity.OrderPaymentJpaEntity;
//import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
//import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
//import com.kijinkai.domain.user.domain.model.User;
//import com.kijinkai.domain.user.domain.model.UserRole;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderItemApplicationServiceTest {
//
//
//    @Mock CustomerPersistencePort customerPersistencePort;
//    @Mock OrderItemPersistencePort orderItemPersistencePort;
//
//    @Mock OrderItemValidator orderItemValidator;
//    @Mock OrderValidator orderValidator;
//    @Mock OrderItemFactory orderItemFactory;
//    @Mock OrderItemMapper orderItemMapper;
//
//    //outer
//    @Mock UserPersistencePort userPersistencePort;
//    @Mock UserApplicationValidator userValidator;
//    @Mock PriceCalculationService priceCalculationService;
//
//    @InjectMocks OrderItemApplicationService orderItemApplicationService;
//
//
//    User user;
//    OrderItem orderItem;
//    Customer customer;
//    OrderPaymentJpaEntity orderPayment;
//    Delivery delivery;
//    Order order;
//    OrderItemRequestDto orderItemRequestDto;
//    OrderItemResponseDto orderItemResponseDto;
//
//
//    @BeforeEach
//    void setUp() {
//
//        user = User.builder().userUuid(UUID.randomUUID()).build();
//
//        customer = Customer.builder().userUuid(user.getUserUuid()).customerUuid(UUID.randomUUID()).build();
//        orderPayment = OrderPaymentJpaEntity.builder().customerUuid(customer.getCustomerUuid()).build();
//        delivery = Delivery.builder().deliveryUuid(UUID.randomUUID()).build();
//        order = Order.builder().orderUuid(UUID.randomUUID()).build();
//
//        orderItemRequestDto = OrderItemRequestDto.builder()
//                .productLink("wwww.aaaaa.com")
//                .quantity(1)
//                .memo("옷 빨간색으로 부탁해요")
//                .priceOriginal(new BigDecimal(10000.00))
//                .build();
//
//
//        orderItem = OrderItem.builder()
//                .orderItemUuid(UUID.randomUUID())
//                .order(order)
//                .customerUuid(customer.getCustomerUuid())
//                .productLink(orderItemRequestDto.getProductLink())
//                .quantity(orderItemRequestDto.getQuantity())
//                .priceOriginal(orderItemRequestDto.getPriceOriginal())
//                .currencyOriginal(Currency.JPY)
//                .orderItemStatus(OrderItemStatus.PENDING)
//                .memo(orderItemRequestDto.getMemo())
//                .build();
//
//
//        orderItemResponseDto = OrderItemResponseDto.builder()
//                .orderItemUuid(orderItem.getOrderItemUuid())
//                .customerUuid(customer.getCustomerUuid())
//                .orderUuid(order.getOrderUuid())
//                .productLink(orderItem.getProductLink())
//                .quantity(orderItem.getQuantity())
//                .memo(orderItem.getMemo())
//                .priceOriginal(orderItem.getPriceOriginal())
//                .orderItemStatus(orderItem.getOrderItemStatus())
//                .build();
//    }
//
//
//    @Test
//    void createOrderItem() {
//
//        BigDecimal convertedAmount = new BigDecimal(10000.00);
//
//        //given
//        when(priceCalculationService.calculateTotalPrice(orderItemRequestDto.getPriceOriginal())).thenReturn(new BigDecimal(10000.00));
//        when(orderItemFactory.createOrderItem(customer,order,convertedAmount, orderItemRequestDto)).thenReturn(orderItem);
//
//        //when
//        OrderItem result = orderItemApplicationService.createOrderItem(customer, order, orderItemRequestDto);
//
//        //then
//        assertThat(result).isNotNull();
//    }
//
//    @Test
//    void secondOrderItemPayment() {
//
//    }
//
//    @Test
//    void deleteOrderItem() {
//        //given
//        when(orderItemPersistencePort.findByOrderItemUuid(orderItem.getOrderItemUuid())).thenReturn(Optional.of(orderItem));
//
//        //when
//        orderItemApplicationService.deleteOrderItem(orderItem.getOrderItemUuid());
//
//        //then
//    }
//
//    @Test
//    void getOrderItems() {
//        //given
//        PageRequest pageable = PageRequest.of(0, 10);
//        List<OrderItem> mockData = List.of(orderItem);
//        PageImpl<OrderItem> mockPage = new PageImpl<>(mockData, pageable, mockData.size());
//
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
//        when(orderItemPersistencePort.findAllByCustomerUuidOrderByOrderCreatedAtDesc(customer.getCustomerUuid(),pageable)).thenReturn(mockPage);
//        when(orderItemMapper.toResponseDto(orderItem)).thenReturn(orderItemResponseDto);
//
//        //when
//        Page<OrderItemResponseDto> result = orderItemApplicationService.getOrderItems(user.getUserUuid(), pageable);
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getMemo()).isEqualTo(orderItem.getMemo());
//
//    }
//
//    @Test
//    void getOrderItemByStatus() {
//        //given
//        PageRequest pageable = PageRequest.of(0, 10);
//        List<OrderItem> mockData = List.of(orderItem);
//        PageImpl<OrderItem> mockPage = new PageImpl<>(mockData, pageable, mockData.size());
//
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
//        when(orderItemPersistencePort.findAllByCustomerUuidAndOrderItemStatusOrderByOrderCreatedAtDesc(customer.getCustomerUuid(),OrderItemStatus.PENDING,pageable)).thenReturn(mockPage);
//        when(orderItemMapper.toResponseDto(orderItem)).thenReturn(orderItemResponseDto);
//
//        //when
//        Page<OrderItemResponseDto> result = orderItemApplicationService.getOrderItemByStatus(user.getUserUuid(), orderItem.getOrderItemStatus(), pageable);
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getOrderItemStatus()).isEqualTo(orderItem.getOrderItemStatus());
//
//    }
//
//    @Test
//    void getOrderItemInfo() {
//        //given
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
//        when(orderItemPersistencePort.findByOrderItemUuid(orderItem.getOrderItemUuid())).thenReturn(Optional.of(orderItem));
//        when(orderItemMapper.toResponseDto(orderItem)).thenReturn(orderItemResponseDto);
//
//        //when
//        OrderItemResponseDto result = orderItemApplicationService.getOrderItemInfo(user.getUserUuid(), orderItem.getOrderItemUuid());
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getMemo()).isEqualTo(orderItemRequestDto.getMemo());
//
//        verify(customerPersistencePort,times(1)).findByUserUuid(user.getUserUuid());
//        verify(orderItemPersistencePort,times(1)).findByOrderItemUuid(orderItem.getOrderItemUuid());
//    }
//
//    @Test
//    void orderItemDashboardCount() {
//        //given
//
//        OrderItemCountResponseDto count = OrderItemCountResponseDto.builder()
//                .allOrderItemCount(3)
//                .pendingCount(1)
//                .pendingApprovalCount(1)
//                .build();
//
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
//        when(orderItemPersistencePort.findOrderItemCountByStatus(customer.getCustomerUuid(), OrderItemStatus.PENDING)).thenReturn(1);
//        when(orderItemPersistencePort.findOrderItemCountByStatus(customer.getCustomerUuid(), OrderItemStatus.PENDING_APPROVAL)).thenReturn(1);
//        when(orderItemPersistencePort.findOrderItemCount(customer.getCustomerUuid())).thenReturn(3);  // 전체는 3개로 하는게 현실적
//        when(orderItemMapper.orderItemDashboardCount(3,1,1)).thenReturn(count);
//
//        //when
//        OrderItemCountResponseDto result = orderItemApplicationService.orderItemDashboardCount(user.getUserUuid());
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getPendingCount()).isEqualTo(1);
//        assertThat(result.getPendingApprovalCount()).isEqualTo(1);
//        assertThat(result.getAllOrderItemCount()).isEqualTo(3);
//    }
//
//    @Test
//    void updateOrderItemWithValidate() {
//        //given
//
//        OrderItemUpdateDto updateDto = OrderItemUpdateDto.builder()
//                .productLink("www.kakao.co.kr")
//                .quantity(10)
//                .memo("파랑색")
//                .priceOriginal(new BigDecimal(10000.00))
//                .build();
//
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
//        when(orderItemPersistencePort.findByOrderItemUuid(orderItem.getOrderItemUuid())).thenReturn(Optional.of(orderItem));
//
//        //when
//
//
//        OrderItem result = orderItemApplicationService.updateOrderItemWithValidate(user.getUserUuid(), orderItem.getOrderItemUuid(), updateDto);
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getProductLink()).isEqualTo(updateDto.getProductLink());
//        assertThat(result.getMemo()).isEqualTo(updateDto.getMemo());
//
//        verify(orderItemValidator, times(1)).validateCustomerOwnershipOfOrderItem(customer, orderItem);
//    }
//
//    @Test
//    void updateOrderItemByAdmin() {
//        //given
//
//        User user = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();
//
//        Customer customer = Customer.builder().userUuid(user.getUserUuid()).customerUuid(UUID.randomUUID()).build();
//        OrderPaymentJpaEntity orderPayment = OrderPaymentJpaEntity.builder().customerUuid(customer.getCustomerUuid()).build();
//        Delivery delivery = Delivery.builder().deliveryUuid(UUID.randomUUID()).build();
//        Order order = Order.builder().orderUuid(UUID.randomUUID()).build();
//
//        OrderItemRequestDto orderItemRequestDto = OrderItemRequestDto.builder()
//                .productLink("wwww.aaaaa.com")
//                .quantity(1)
//                .memo("옷 빨간색으로 부탁해요")
//                .priceOriginal(new BigDecimal(10000.00))
//                .build();
//
//
//        OrderItem orderItem = OrderItem.builder()
//                .orderItemUuid(UUID.randomUUID())
//                .order(order)
//                .customerUuid(customer.getCustomerUuid())
//                .productLink(orderItemRequestDto.getProductLink())
//                .quantity(orderItemRequestDto.getQuantity())
//                .priceOriginal(orderItemRequestDto.getPriceOriginal())
//                .currencyOriginal(Currency.JPY)
//                .memo(orderItemRequestDto.getMemo())
//                .build();
//
//
//        OrderItemUpdateDto updateDto = OrderItemUpdateDto.builder()
//                .productLink("www.kakao.co.kr")
//                .quantity(10)
//                .memo("파랑색")
//                .priceOriginal(new BigDecimal(10000.00))
//                .build();
//
//
//        when(userPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(user));
//        when(orderItemPersistencePort.findByOrderItemUuid(orderItem.getOrderItemUuid())).thenReturn(Optional.of(orderItem));
//
//        //when
//        OrderItem result = orderItemApplicationService.updateOrderItemByAdmin(user.getUserUuid(), orderItem.getOrderItemUuid(), updateDto);
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getProductLink()).isEqualTo(orderItem.getProductLink());
//        assertThat(result.getMemo()).isEqualTo(orderItem.getMemo());
//    }
//
//
//
//    @Test
//    void firstOrderItemPayment() {
//        //given
//
//        //when
//
//        //then
//    }
//
//
//}