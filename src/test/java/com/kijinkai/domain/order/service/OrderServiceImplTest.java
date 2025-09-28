//package com.kijinkai.domain.order.service;
//
//import com.kijinkai.domain.customer.adapter.out.persistence.entity.Customer;
//import com.kijinkai.domain.customer.adapter.out.persistence.repository.CustomerRepository;
//import com.kijinkai.domain.exchange.doamin.Currency;
//import com.kijinkai.domain.exchange.service.PriceCalculationService;
//import com.kijinkai.domain.order.dto.OrderRequestDto;
//import com.kijinkai.domain.order.dto.OrderResponseDto;
//import com.kijinkai.domain.order.dto.OrderUpdateDto;
//import com.kijinkai.domain.order.entity.Order;
//import com.kijinkai.domain.order.entity.OrderStatus;
//import com.kijinkai.domain.order.exception.OrderCreationException;
//import com.kijinkai.domain.order.factory.OrderFactory;
//import com.kijinkai.domain.order.mapper.OrderMapper;
//import com.kijinkai.domain.order.repository.OrderRepository;
//import com.kijinkai.domain.order.validator.OrderValidator;
//import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
//import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
//import com.kijinkai.domain.orderitem.entity.OrderItem;
//import com.kijinkai.domain.orderitem.exception.OrderUpdateException;
//import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
//import com.kijinkai.domain.orderitem.service.OrderItemService;
//import com.kijinkai.domain.payment.domain.exception.PaymentProcessingException;
//import com.kijinkai.domain.transaction.entity.TransactionStatus;
//import com.kijinkai.domain.transaction.entity.TransactionType;
//import com.kijinkai.domain.transaction.service.TransactionService;
//import com.kijinkai.domain.user.domain.model.UserRole;
//import com.kijinkai.domain.user.domain.exception.UserRoleValidateException;
//import com.kijinkai.domain.user.adapter.in.web.validator.UserValidator;
//import com.kijinkai.domain.wallet.entity.Wallet;
//import com.kijinkai.domain.wallet.entity.WalletStatus;
//import com.kijinkai.domain.wallet.exception.WalletUpdateFailedException;
//import com.kijinkai.domain.wallet.repository.WalletRepository;
//import com.kijinkai.domain.wallet.validator.WalletValidator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.OptimisticLockingFailureException;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.function.Supplier;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("주문 서비스 테스트")
//class OrderServiceImplTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//    @Mock
//    private OrderItemRepository orderItemRepository;
//    @Mock
//    private WalletRepository walletRepository;
//    @Mock
//    private CustomerRepository customerRepository;
//    @Mock
//    private OrderMapper orderMapper;
//    @Mock
//    private OrderFactory orderFactory;
//    @Mock
//    private WalletValidator walletValidator;
//    @Mock
//    private UserValidator userValidator;
//    @Mock
//    private OrderValidator orderValidator;
//    @Mock
//    private OrderItemService orderItemService;
//    @Mock
//    private PriceCalculationService priceCalculationService;
//    @Mock
//    private TransactionService transactionService;
//
//    @InjectMocks
//    private OrderServiceImpl orderService;
//
//    private UUID userUuid;
//    private UUID orderUuid;
//    private Customer customer;
//    private Order order;
//    private Wallet wallet;
//    private OrderRequestDto orderRequestDto;
//    private OrderResponseDto orderResponseDto;
//
//    @BeforeEach
//    void setUp() {
//        userUuid = UUID.randomUUID();
//        orderUuid = UUID.randomUUID();
//
//        User user = User.builder()
//                .userUuid(userUuid)
//                .userRole(UserRole.USER)
//                .build();
//
//        customer = Customer.builder()
//                .customerUuid(UUID.randomUUID())
//                .user(user)
//                .build();
//
//        order = Order.builder()
//                .orderUuid(orderUuid)
//                .customer(customer)
//                .orderStatus(OrderStatus.DRAFT)
//                .build();
//
//        wallet = Wallet.builder()
//                .walletUuid(UUID.randomUUID())
//                .customer(customer)
//                .balance(new BigDecimal("1000.00"))
//                .walletStatus(WalletStatus.ACTIVE)
//                .build();
//
//        orderRequestDto = OrderRequestDto.builder()
//                .memo("테스트 주문")
//                .orderItems(Arrays.asList(
//                        OrderItemRequestDto.builder()
//                                .productLink("http://example.com/product1")
//                                .build()
//                ))
//                .build();
//
//        orderResponseDto = OrderResponseDto.builder()
//                .orderUuid(orderUuid)
//                .memo("테스트 주문")
//                .build();
//    }
//
//    @Nested
//    @DisplayName("주문 생성 테스트")
//    class CreateOrderTest {
//
//        @Test
//        @DisplayName("성공: 정상적인 주문 생성")
//        void createOrderProcess_Success() {
//            // Given
//            OrderItem orderItem = OrderItem.builder().build();
//
//            when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.of(customer));
//            when(orderFactory.createOrder(customer, orderRequestDto.getMemo())).thenReturn(order);
//            when(orderRepository.save(order)).thenReturn(order);
//            when(orderItemService.createOrderItem(eq(customer), eq(order), any())).thenReturn(orderItem);
//            when(orderMapper.toResponse(order)).thenReturn(orderResponseDto);
//
//            // When
//            OrderResponseDto result = orderService.createOrderProcess(userUuid, orderRequestDto);
//
//            // Then
//            assertThat(result).isEqualTo(orderResponseDto);
//            verify(orderItemRepository).saveAll(anyList());
//        }
//
//        @Test
//        @DisplayName("실패: 고객을 찾을 수 없음")
//        void createOrderProcess_CustomerNotFound() {
//            // Given
//            when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.empty());
//
//            // When & Then
//            assertThatThrownBy(() -> orderService.createOrderProcess(userUuid, orderRequestDto)).isInstanceOf(OrderCreationException.class);
//        }
//    }
//
//    @Nested
//    @DisplayName("견적 업데이트 테스트")
//    class UpdateOrderEstimateTest {
//
//        private OrderUpdateDto updateDto;
//        private User adminUser;
//        private Customer adminCustomer;
//
//        @BeforeEach
//        void setUp() {
//            adminUser = User.builder()
//                    .userUuid(userUuid)
//                    .userRole(UserRole.ADMIN)
//                    .build();
//
//            adminCustomer = Customer.builder()
//                    .customerUuid(UUID.randomUUID())
//                    .user(adminUser)
//                    .build();
//
//            updateDto = OrderUpdateDto.builder()
//                    .memo("업데이트된 메모")
//                    .convertedCurrency(Currency.KRW)
//                    .orderItems(Arrays.asList(
//                            OrderItemUpdateDto.builder()
//                                    .orderItemUuid(UUID.randomUUID())
//                                    .build()
//                    ))
//                    .build();
//        }
//
//        @Test
//        @DisplayName("성공: 관리자가 견적 업데이트")
//        void updateOrderEstimate_Success() {
//            // Given
//            OrderItem orderItem = OrderItem.builder()
//                    .orderItemUuid(UUID.randomUUID())
//                    .order(order)
//                    .priceOriginal(new BigDecimal("100.00"))
//                    .build();
//
//            when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.of(adminCustomer));
//            when(orderRepository.findByOrderUuid(orderUuid)).thenReturn(Optional.of(order));
//            when(orderItemRepository.findByOrderItemUuid(any())).thenReturn(Optional.of(orderItem));
//            when(priceCalculationService.calculateTotalPrice(any(), any(), any())).thenReturn(new BigDecimal("130000.00"));
//            when(orderRepository.save(order)).thenReturn(order);
//            when(orderMapper.toResponse(order)).thenReturn(orderResponseDto);
//
//            // When
//            OrderResponseDto result = orderService.updateOrderEstimate(userUuid, orderUuid, updateDto);
//
//            // Then
//            assertThat(result).isEqualTo(orderResponseDto);
//            verify(userValidator).requireAdminRole(adminUser);
//            verify(orderValidator).requireDraftOrderStatus(order);
//        }
//
//        @Test
//        @DisplayName("실패: 관리자 권한 없음")
//        void updateOrderEstimate_NotAdmin() {
//            // Given
//            when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.of(customer));
//            doThrow(new UserRoleValidateException("관리자 권한이 필요합니다")).when(userValidator).requireAdminRole(customer.getUser());
//
//            // When & Then
//            assertThatThrownBy(() -> orderService.updateOrderEstimate(userUuid, orderUuid, updateDto))
//                    .isInstanceOf(OrderUpdateException.class);
//        }
//    }
//
//    @Nested
//    @DisplayName("주문 완료 테스트")
//    class CompleteOrderTest {
//
//        @BeforeEach
//        void setUp() {
//            order = Order.builder()
//                    .orderUuid(orderUuid)
//                    .customer(customer)
//                    .orderStatus(OrderStatus.AWAITING_PAYMENT)
//                    .totalPriceOriginal(new BigDecimal("500.00"))
//                    .build();
//        }
//
//        @Test
//        @DisplayName("성공: 정상적인 주문 완료")
//        void completedOrder_Success() {
//            // Given
//            Wallet updatedWallet = Wallet.builder()
//                    .walletUuid(wallet.getWalletUuid())
//                    .customer(customer)
//                    .balance(new BigDecimal("500.00"))
//                    .walletStatus(WalletStatus.ACTIVE)
//                    .build();
//
//            when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.of(customer));
//            when(orderRepository.findByCustomerUuidAndOrderUuid(customer.getCustomerUuid(), orderUuid)).thenReturn(Optional.of(order));
//            when(walletRepository.findByCustomerCustomerId(customer.getCustomerId())).thenReturn(Optional.of(wallet));
//            when(walletRepository.decreaseBalanceAtomic(wallet.getWalletUuid(), order.getTotalPriceOriginal())).thenReturn(1);
//            when(orderRepository.save(order)).thenReturn(order);
//            when(walletRepository.findByWalletId(wallet.getWalletId())).thenReturn(Optional.of(updatedWallet));
//            when(orderMapper.toResponse(order)).thenReturn(orderResponseDto);
//
//            // When
//            OrderResponseDto result = orderService.completedOrder(userUuid, orderUuid);
//
//            // Then
//            assertThat(result).isEqualTo(orderResponseDto);
//            verify(orderValidator).requireAwaitingOrderStatus(order);
//            verify(walletValidator).requireActiveStatus(wallet);
//            verify(walletValidator).requireSufficientBalance(wallet, order.getTotalPriceOriginal());
//            verify(transactionService).createTransactionWithValidate(
//                    eq(userUuid), eq(wallet), eq(order), eq(TransactionType.PAYMENT),
//                    eq(order.getTotalPriceOriginal()), eq(wallet.getBalance()),
//                    eq(updatedWallet.getBalance()), eq(TransactionStatus.COMPLETED));
//        }
//
//        @Test
//        @DisplayName("실패: 잔액 부족")
//        void completedOrder_InsufficientBalance() {
//            // Given
//            when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.of(customer));
//            when(orderRepository.findByCustomerUuidAndOrderUuid(customer.getCustomerUuid(), orderUuid)).thenReturn(Optional.of(order));
//            when(walletRepository.findByCustomerCustomerId(customer.getCustomerId())).thenReturn(Optional.of(wallet));
//            when(walletRepository.decreaseBalanceAtomic(wallet.getWalletUuid(), order.getTotalPriceOriginal())).thenReturn(0); // 업데이트 실패
//
//            // When & Then
//            assertThatThrownBy(() -> orderService.completedOrder(userUuid, orderUuid))
//                    .isInstanceOf(WalletUpdateFailedException.class)
//                    .hasMessage("Insufficient balance for payment or wallet update failed");
//        }
//
//        @Test
//        @DisplayName("성공: 낙관적 락 재시도")
//        void completedOrder_OptimisticLockRetry() {
//            // Given
//            when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.of(customer));
//            when(orderRepository.findByCustomerUuidAndOrderUuid(customer.getCustomerUuid(), orderUuid)).thenReturn(Optional.of(order));
//            when(walletRepository.findByCustomerCustomerId(customer.getCustomerId())).thenReturn(Optional.of(wallet));
//
//            // 첫 번째 호출에서 OptimisticLockingFailureException 발생, 두 번째 호출에서 성공
//            when(walletRepository.decreaseBalanceAtomic(wallet.getWalletUuid(),
//                    order.getTotalPriceOriginal()))
//                    .thenThrow(new OptimisticLockingFailureException("Lock failure"))
//                    .thenReturn(1);
//
//            when(orderRepository.save(order)).thenReturn(order);
//            when(walletRepository.findByWalletId(wallet.getWalletId())).thenReturn(Optional.of(wallet));
//            when(orderMapper.toResponse(order)).thenReturn(orderResponseDto);
//
//            // When
//            OrderResponseDto result = orderService.completedOrder(userUuid, orderUuid);
//
//            // Then
//            assertThat(result).isEqualTo(orderResponseDto);
//            verify(walletRepository, times(2))
//                    .decreaseBalanceAtomic(wallet.getWalletUuid(), order.getTotalPriceOriginal());
//        }
//    }
//
//    @Nested
//    @DisplayName("주문 취소 테스트")
//    class CancelOrderTest {
//
//        @Test
//        @DisplayName("성공: 주문 취소")
//        void cancelOrder_Success() {
//            // Given
//            when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.of(customer));
//            when(orderRepository.findByCustomerUuidAndOrderUuid(
//                    customer.getCustomerUuid(), orderUuid)).thenReturn(Optional.of(order));
//            when(orderMapper.toResponse(order)).thenReturn(orderResponseDto);
//
//            // When
//            OrderResponseDto result = orderService.cancelOrder(userUuid, orderUuid);
//
//            // Then
//            assertThat(result).isEqualTo(orderResponseDto);
//            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
//
//
//            verify(orderValidator).requireCancellableStatus(order);
//        }
//    }
//
//    @Nested
//    @DisplayName("주문 삭제 테스트")
//    class DeleteOrderTest {
//
//        private User adminUser;
//        private Customer adminCustomer;
//
//        @BeforeEach
//        void setUp() {
//            adminUser = User.builder()
//                    .userUuid(userUuid)
//                    .userRole(UserRole.ADMIN)
//                    .build();
//
//            adminCustomer = Customer.builder()
//                    .customerUuid(UUID.randomUUID())
//                    .user(adminUser)
//                    .build();
//        }
//
//        @Test
//        @DisplayName("성공: 관리자가 주문 삭제")
//        void deleteOrder_Success() {
//            // Given
//            when(customerRepository.findByUserUuid(userUuid)).thenReturn(Optional.of(adminCustomer));
//            when(orderRepository.findByCustomerUuidAndOrderUuid(
//                    adminCustomer.getCustomerUuid(), orderUuid)).thenReturn(Optional.of(order));
//
//            // When
//            orderService.deleteOrder(userUuid, orderUuid);
//
//            // Then
//            verify(userValidator).requireAdminRole(adminUser);
//            verify(orderValidator).requireCancellableStatus(order);
//            verify(orderRepository).delete(order);
//        }
//    }
//
//    @Nested
//    @DisplayName("낙관적 락 재시도 테스트")
//    class OptimisticLockRetryTest {
//
//        @Test
//        @DisplayName("성공: 최대 재시도 횟수 내 성공")
//        void executeWithOptimisticLockRetry_SuccessAfterRetry() {
//            // Given
//            Supplier<OrderResponseDto> operation = mock(Supplier.class);
//            when(operation.get())
//                    .thenThrow(new OptimisticLockingFailureException("첫 번째 실패"))
//                    .thenReturn(orderResponseDto);
//
//            // When
//            OrderResponseDto result = orderService.executeWithOptimisticLockRetry(operation);
//
//            // Then
//            assertThat(result).isEqualTo(orderResponseDto);
//            verify(operation, times(2)).get();
//        }
//
//        @Test
//        @DisplayName("실패: 최대 재시도 횟수 초과")
//        void executeWithOptimisticLockRetry_MaxRetriesExceeded() {
//            // Given
//            Supplier<OrderResponseDto> operation = mock(Supplier.class);
//            when(operation.get()).thenThrow(new OptimisticLockingFailureException("계속 실패"));
//
//            // When & Then
//            assertThatThrownBy(() -> orderService.executeWithOptimisticLockRetry(operation))
//                    .isInstanceOf(PaymentProcessingException.class)
//                    .hasMessage("Payment completion failed due to concurrent access");
//
//            verify(operation, times(3)).get(); // 최대 3번 시도
//        }
//    }
//}