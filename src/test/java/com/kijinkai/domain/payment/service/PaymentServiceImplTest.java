//package com.kijinkai.domain.payment.service;
//
//import com.kijinkai.domain.customer.entity.Customer;
//import com.kijinkai.domain.customer.repository.CustomerRepository;
//import com.kijinkai.domain.exchange.service.PriceCalculationService;
//import com.kijinkai.domain.order.entity.Order;
//import com.kijinkai.domain.orderitem.entity.OrderItem;
//import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
//import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
//import com.kijinkai.domain.payment.application.dto.PaymentDepositRequestDto;
//import com.kijinkai.domain.payment.application.dto.PaymentResponseDto;
//import com.kijinkai.domain.payment.application.dto.WithdrawalRequestDto;
//import com.kijinkai.domain.payment.domain.enums.PaymentStatus;
//import com.kijinkai.domain.payment.domain.exception.PaymentStatusException;
//import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
//import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
//import com.kijinkai.domain.payment.domain.validator.PaymentValidator;
//import com.kijinkai.domain.user.entity.User;
//import com.kijinkai.domain.user.entity.UserRole;
//import com.kijinkai.domain.user.exception.UserRoleValidateException;
//import com.kijinkai.domain.user.repository.UserRepository;
//import com.kijinkai.domain.user.validator.UserValidator;
//import com.kijinkai.domain.wallet.entity.Wallet;
//import com.kijinkai.domain.wallet.entity.WalletStatus;
//import com.kijinkai.domain.wallet.exception.InactiveWalletException;
//import com.kijinkai.domain.wallet.exception.InsufficientBalanceException;
//import com.kijinkai.domain.wallet.exception.WalletNotFoundException;
//import com.kijinkai.domain.wallet.repository.WalletRepository;
//import com.kijinkai.domain.wallet.service.WalletService;
//import com.kijinkai.domain.wallet.validator.WalletValidator;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.orm.ObjectOptimisticLockingFailureException;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PaymentServiceImplTest {
//
//    @Mock
//    private WalletService walletService;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private WalletRepository walletRepository;
//    @Mock
//    private CustomerRepository customerRepository;
//    @Mock
//    private OrderItemRepository orderItemRepository;
//    @Mock
//    private PaymentMapper paymentMapper;
//    @Mock
//    private UserValidator userValidator;
//    @Mock
//    private PaymentValidator paymentValidator;
//    @Mock
//    private WalletValidator walletValidator;
//    @Mock
//    private PaymentFactory paymentFactory;
//    @Mock
//    private PriceCalculationService priceCalculationService;
//
//    @InjectMocks
//    private PaymentServiceImpl paymentService;
//
//    private UUID userUuid;
//    private UUID adminUuid;
//    private UUID customerUuid;
//    private UUID walletUuid;
//    private String paymentUuid;
//    private User user;
//    private Customer customer;
//    private Wallet wallet;
//    private Payment payment;
//    private User adminUser;
//    private Order order;
//    private OrderItem orderItem;
//    private PaymentDepositRequestDto depositRequestDto;
//    private WithdrawalRequestDto withdrawalRequestDto;
//
//    @BeforeEach
//    void setUp() {
//        userUuid = UUID.randomUUID();
//        adminUuid = UUID.randomUUID();
//        customerUuid = UUID.randomUUID();
//        walletUuid = UUID.randomUUID();
//        paymentUuid = UUID.randomUUID().toString();
//
//        adminUser = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();
//
//        user = User.builder().userUuid(userUuid).build();
//        customer = Customer.builder().user(user).build();
//        wallet = Wallet.builder().walletUuid(walletUuid).customer(customer).balance(new BigDecimal(500000.00)).walletStatus(WalletStatus.ACTIVE).build();
//        payment = Payment.builder().customer(customer).wallet(wallet).paymentUuid(UUID.fromString(paymentUuid)).paymentStatus(PaymentStatus.PENDING).amountOriginal(new BigDecimal(50000.00)).build();
//        order = Order.builder().customer(customer).build();
//        orderItem = OrderItem.builder().orderItemUuid(UUID.randomUUID()).priceOriginal(new BigDecimal(5000.00)).order(order).build();
//
//        // DTO 생성
//        depositRequestDto = PaymentDepositRequestDto.builder()
//                .amountOriginal(new BigDecimal("10000"))
//                .build();
//
//        withdrawalRequestDto = WithdrawalRequestDto.builder()
//                .amountOriginal(new BigDecimal("25000"))
//                .build();
//
//        //공통 스터빙
//
//    }
//
//    // Helper method
//    private User createMockUser(UUID uuid, UserRole userRole) {
//        return User.builder().userUuid(uuid).userRole(userRole).build();
//    }
//
//    private Customer createMockCustomer(User user) {
//        return Customer.builder().user(user).build();
//    }
//
//    private Wallet createMockWallet(UUID walletUuid, Customer customer, BigDecimal balance, WalletStatus status) {
//        return Wallet.builder().walletUuid(walletUuid).customer(customer).balance(balance).walletStatus(status).build();
//    }
//
//    private Payment createMockPayment(Customer customer, Wallet wallet, UUID paymentUuid, PaymentStatus status, BigDecimal amount) {
//        return Payment.builder().customer(customer).paymentUuid(paymentUuid).paymentStatus(status).amountOriginal(amount).build();
//    }
//
//
//    @Test
//    @DisplayName("성공적인 입금 결제 생성")
//    void createDepositPayment_Success() {
//
//        // Given
//        when(walletRepository.findByUserUserUuidWithCustomerAndUser(userUuid)).thenReturn(Optional.ofNullable(wallet));
//        when(priceCalculationService.convertAndCalculateTotalInJpy(any(), any(), any())).thenReturn(new BigDecimal("10200"));
//        when(paymentFactory.createWithDepositPayment(any(), any(), any(), any(), any())).thenReturn(payment);
//        when(paymentRepository.save(any())).thenReturn(payment);
//        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponseDto());
//
//
//        // When
//        PaymentResponseDto result = paymentService.createDepositPayment(userUuid, depositRequestDto);
//
//        // Then
//        assertThat(result).isNotNull();
//        verify(walletValidator).requireActiveStatus(wallet);
//        verify(paymentValidator).validateAmount(depositRequestDto);
//        verify(paymentRepository).save(payment);
//
//    }
//
//    @Test
//    @DisplayName("비활성 지갑으로 입금 결제 생성 실패")
//    void createDepositPayment_InactiveWallet_ThrowsException() {
//        // Given
//        when(walletRepository.findByUserUserUuidWithCustomerAndUser(userUuid)).thenReturn(Optional.of(wallet));
//        doThrow(new InactiveWalletException("Wallet is inactive")).when(walletValidator).requireActiveStatus(wallet);
//
//
//        // When & Then
//        assertThatThrownBy(() -> paymentService.createDepositPayment(userUuid, depositRequestDto))
//                .isInstanceOf(InactiveWalletException.class)
//                .hasMessage("Wallet is inactive");
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 지갑으로 입금 결제 생성 실패")
//    void createDepositPayment_WalletNotFound_ThrowsException() {
//        // Given
//        when(walletRepository.findByUserUserUuidWithCustomerAndUser(userUuid)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> paymentService.createDepositPayment(userUuid, depositRequestDto))
//                .isInstanceOf(WalletNotFoundException.class)
//                .hasMessageContaining("Wallet not found for userUuid");
//    }
//
//    @Test
//    @DisplayName("관리자에 의한 입금 결제 완료 성공")
//    void completeDepositPayment_Success() {
//        // Given
//        when(userRepository.findByUserUuid(adminUuid)).thenReturn(Optional.of(adminUser));
//        when(paymentRepository.findByPaymentUuidWithCustomerAndWallet(any())).thenReturn(Optional.of(payment));
//        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponseDto());
//
//
//        // When
//        PaymentResponseDto result = paymentService.completeDepositPayment(adminUuid, paymentUuid);
//
//        // Then
//        assertThat(result).isNotNull();
//        verify(userValidator).requireAdminRole(adminUser);
//        verify(walletService).deposit(any(), any(), any());
//        verify(paymentRepository).save(payment);
//    }
//
//    @Test
//    @DisplayName("관리자 권한 없이 입금 결제 완료 시도 실패")
//    void completeDepositPayment_NoAdminRole_ThrowsException() {
//
//        // Given
//        User admin = createMockUser(UUID.randomUUID(), UserRole.ADMIN);
//
//        when(userRepository.findByUserUuid(adminUuid)).thenReturn(Optional.of(admin));
//        doThrow(new UserRoleValidateException("Admin role required")).when(userValidator).requireAdminRole(admin);
//
//        // When & Then
//        assertThatThrownBy(() -> paymentService.completeDepositPayment(adminUuid, paymentUuid))
//                .isInstanceOf(UserRoleValidateException.class)
//                .hasMessage("Admin role required");
//    }
//
//    @Test
//    @DisplayName("낙관적 락 실패 시 재시도 후 성공")
//    void completeDepositPayment_OptimisticLockRetry_Success() {
//        // Given
//        when(userRepository.findByUserUuid(adminUuid)).thenReturn(Optional.of(adminUser));
//        when(paymentRepository.findByPaymentUuidWithCustomerAndWallet(any())).thenReturn(Optional.of(payment));
//        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponseDto());
//
//
//        // 첫 번째 시도에서 락 실패, 두 번째 시도에서 성공
//        doThrow(new ObjectOptimisticLockingFailureException("", new RuntimeException()))
//                .doNothing()
//                .when(walletService).deposit(any(), any(), any());
//
//        // When
//        PaymentResponseDto result = paymentService.completeDepositPayment(adminUuid, paymentUuid);
//
//        // Then
//        assertThat(result).isNotNull();
//        verify(walletService, times(2)).deposit(any(), any(), any());
//    }
//
//    @Test
//    @DisplayName("성공적인 출금 결제 생성")
//    void createWithdrawalPayment_Success() {
//
//        // Given
//        when(customerRepository.findByUserUserUuid(userUuid)).thenReturn(Optional.of(customer));
//        when(walletRepository.findByCustomerCustomerId(any())).thenReturn(Optional.of(wallet));
//        when(priceCalculationService.convertAndCalculateTotalInLocalCurrency(any(), any(), any())).thenReturn(new BigDecimal("25300"));
//        when(paymentFactory.createWithWithdrawalPayment(any(), any(), any(), any(), any())).thenReturn(payment);
//        when(paymentRepository.save(any())).thenReturn(payment);
//        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponseDto());
//
//        // When
//        PaymentResponseDto result = paymentService.createWithdrawalPayment(userUuid, withdrawalRequestDto);
//
//        // Then
//        assertThat(result).isNotNull();
//        verify(walletValidator).requireActiveStatus(wallet);
//        verify(walletValidator).validateMinimumExchangeAmount(wallet);
//        verify(paymentValidator).validateAmountByWithdrawal(withdrawalRequestDto);
//    }
//
//    @Test
//    @DisplayName("잔액 부족으로 출금 결제 생성 실패")
//    void createWithdrawalPayment_InsufficientBalance_ThrowsException() {
//
//
//        User user = createMockUser(UUID.randomUUID(), UserRole.USER);
//        Customer customer = createMockCustomer(user);
//        Wallet wallet = createMockWallet(UUID.randomUUID(), customer, BigDecimal.ZERO, WalletStatus.FROZEN);
//
//        // Given
//        when(customerRepository.findByUserUserUuid(userUuid)).thenReturn(Optional.of(this.customer));
//        when(walletRepository.findByCustomerCustomerId(any())).thenReturn(Optional.of(wallet));
//
//        // When & Then
//        assertThatThrownBy(() -> paymentService.createWithdrawalPayment(userUuid, withdrawalRequestDto))
//                .isInstanceOf(InsufficientBalanceException.class)
//                .hasMessage("Insufficient balance including fees");
//    }
//
//    @Test
//    @DisplayName("결제 취소 성공")
//    void cancelPayment_Success() {
//
//        // Given
//        when(customerRepository.findByUserUserUuid(userUuid)).thenReturn(Optional.of(customer));
//        when(paymentRepository.findByCustomerCustomerUuidAndPaymentUuidWithCustomerAndWallet(any(), any())).thenReturn(Optional.of(payment));
//        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponseDto());
//
//        payment.cancelPayment();
//
//        // When
//        PaymentResponseDto result = paymentService.cancelPayment(userUuid, paymentUuid);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
//
//        verify(paymentValidator).requiredPendingStatus(payment);
//        verify(paymentRepository).save(payment);
//    }
//
//    @Test
//    @DisplayName("환불 처리 성공")
//    void refundPayment_Success() {
//        // Given
//        String orderItemUuid = UUID.randomUUID().toString();
//        String reason = "재고 부족";
//
//        when(userRepository.findByUserUuid(adminUuid)).thenReturn(Optional.of(adminUser));
//        when(orderItemRepository.findByOrderItemUuidWithOrderAndCustomer(any())).thenReturn(Optional.of(orderItem));
//        when(walletRepository.findByCustomerCustomerId(any())).thenReturn(Optional.of(wallet));
//        when(paymentFactory.createWithRefundPayment(any(), any(), any(), any(), any())).thenReturn(payment);
//        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponseDto());
//
//
//        // When
//        PaymentResponseDto result = paymentService.refundPayment(adminUuid, orderItemUuid, reason);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(orderItem.getOrderItemStatus()).isEqualTo(OrderItemStatus.CANCELLED);
//
//        verify(userValidator, times(2)).requireAdminRole(adminUser);
//        verify(walletService).deposit(any(), any(), any());
//        verify(paymentRepository).save(payment);
//    }
//
//    @Test
//    @DisplayName("결제 정보 조회 성공")
//    void getPaymentInfo_Success() {
//        // Given
//        when(customerRepository.findByUserUserUuid(userUuid)).thenReturn(Optional.of(customer));
//        when(paymentRepository.findByCustomerCustomerUuidAndPaymentUuidWithCustomerAndWallet(any(), any())).thenReturn(Optional.of(payment));
//        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponseDto());
//
//
//        // When
//        PaymentResponseDto result = paymentService.getPaymentInfo(userUuid, paymentUuid);
//
//        // Then
//        assertThat(result).isNotNull();
//        verify(paymentMapper).toResponse(payment);
//    }
//
//
//    // 추가 테스트 케이스들
//    @Test
//    @DisplayName("비활성 지갑 상태로 출금 결제 완료 시도 실패")
//    void completePaymentByWithdrawal_InactiveWallet_ThrowsException() {
//        // Given
////
////        User user = createMockUser(UUID.randomUUID(), UserRole.USER);
////        Customer customer = createMockCustomer(user);
////        Wallet wallet = createMockWallet(UUID.randomUUID(), customer, BigDecimal.ZERO, WalletStatus.FROZEN);
////        Payment payment = createMockPayment(customer, wallet, UUID.randomUUID(), PaymentStatus.COMPLETED, new BigDecimal(10000.00));
//
//
//        Wallet wallet1 = Wallet.builder().walletUuid(walletUuid).customer(customer).walletStatus(WalletStatus.FROZEN).build();
//        Payment payment1 = Payment.builder().paymentUuid(UUID.fromString(paymentUuid)).wallet(wallet1).build();
//
//        when(userRepository.findByUserUuid(adminUuid)).thenReturn(Optional.of(adminUser));
//        when(paymentRepository.findByPaymentUuidWithCustomerAndWallet(any())).thenReturn(Optional.of(payment1));
//
//
//        // When & Then
//        assertThatThrownBy(() -> paymentService.completePaymentByWithdrawal(adminUuid, paymentUuid))
//                .isInstanceOf(InactiveWalletException.class)
//                .hasMessage("Wallet is inactive");
//    }
//
//    @Test
//    @DisplayName("완료된 상태의 결제 취소 시도 실패")
//    void cancelPayment_CompletedPayment_ThrowsException() {
//        // Given
//
//        User user = createMockUser(UUID.randomUUID(), UserRole.USER);
//        Customer customer = createMockCustomer(user);
//        Wallet wallet = createMockWallet(UUID.randomUUID(), customer, BigDecimal.ZERO, WalletStatus.ACTIVE);
//        Payment payment = createMockPayment(customer, wallet, UUID.randomUUID(), PaymentStatus.COMPLETED, new BigDecimal(10000.00));
//
//        when(customerRepository.findByUserUserUuid(userUuid)).thenReturn(Optional.of(customer));
//        when(paymentRepository.findByCustomerCustomerUuidAndPaymentUuidWithCustomerAndWallet(any(), any())).thenReturn(Optional.of(payment));
//        doThrow(new PaymentStatusException("Payment is not in pending status")).when(paymentValidator).requiredPendingStatus(payment);
//
//        // When & Then
//        assertThatThrownBy(() -> paymentService.cancelPayment(userUuid, paymentUuid))
//                .isInstanceOf(PaymentStatusException.class)
//                .hasMessage("Payment is not in pending status");
//    }
//
//    @Test
//    @DisplayName("큰 금액의 환불 처리 성공")
//    void refundPayment_LargeAmount_Success() {
//        // Given
//        String orderItemUuid = UUID.randomUUID().toString();
//        String reason = "상품 결함";
//        BigDecimal largeAmount = new BigDecimal("100000");
//        OrderItem orderItem1 = OrderItem.builder().order(order).orderItemUuid(UUID.fromString(orderItemUuid)).priceOriginal(largeAmount).build();
//
//        when(userRepository.findByUserUuid(adminUuid)).thenReturn(Optional.of(adminUser));
//        when(orderItemRepository.findByOrderItemUuidWithOrderAndCustomer(any()))
//                .thenReturn(Optional.of(orderItem1));
//        when(walletRepository.findByCustomerCustomerId(any())).thenReturn(Optional.of(wallet));
//        when(paymentFactory.createWithRefundPayment(any(), any(), any(), any(), any())).thenReturn(payment);
//        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponseDto());
//
//
//        // When
//        PaymentResponseDto result = paymentService.refundPayment(adminUuid, orderItemUuid, reason);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(orderItem1.getOrderItemStatus()).isEqualTo(OrderItemStatus.CANCELLED);
//
//        verify(walletService).deposit(any(), any(), eq(largeAmount));
//
//    }
//}