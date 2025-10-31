//package com.kijinkai.domain.payment.application.service;
//
//import com.kijinkai.domain.customer.adapter.out.persistence.entity.Customer;
//import com.kijinkai.domain.exchange.doamin.Currency;
//import com.kijinkai.domain.exchange.service.PriceCalculationService;
//import com.kijinkai.domain.order.entity.OrderJpaEntity;
//import com.kijinkai.domain.orderitem.entity.OrderItem;
//import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
//import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
//import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
//import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
//import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
//import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;
//import com.kijinkai.domain.payment.application.dto.response.RefundResponseDto;
//import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;
//import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
//import com.kijinkai.domain.payment.application.port.out.*;
//import com.kijinkai.domain.payment.adapter.out.persistence.entity.DepositRequestJpaEntity;
//import com.kijinkai.domain.payment.adapter.out.persistence.entity.OrderPaymentJpaEntity;
//import com.kijinkai.domain.payment.adapter.out.persistence.entity.RefundRequestJpaEntity;
//import com.kijinkai.domain.payment.adapter.out.persistence.entity.WithdrawRequestJpaEntity;
//import com.kijinkai.domain.payment.domain.enums.DepositStatus;
//import com.kijinkai.domain.payment.domain.enums.RefundType;
//import com.kijinkai.domain.payment.domain.exception.*;
//import com.kijinkai.domain.payment.domain.repository.DepositRequestRepository;
//import com.kijinkai.domain.payment.domain.repository.OrderPaymentRepository;
//import com.kijinkai.domain.payment.domain.repository.RefundRequestRepository;
//import com.kijinkai.domain.payment.domain.repository.WithdrawRequestRepository;
//import com.kijinkai.domain.payment.domain.service.DepositRequestService;
//import com.kijinkai.domain.payment.domain.service.OrderPaymentService;
//import com.kijinkai.domain.payment.domain.service.RefundRequestService;
//import com.kijinkai.domain.payment.domain.service.WithdrawRequestService;
//import com.kijinkai.domain.user.domain.model.User;
//import com.kijinkai.domain.wallet.dto.WalletResponseDto;
//import com.kijinkai.domain.wallet.entity.WalletJpaEntity;
//import com.kijinkai.domain.wallet.exception.InsufficientBalanceException;
//import com.kijinkai.domain.wallet.exception.WalletNotActiveException;
//import jakarta.persistence.OptimisticLockException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PaymentApplicationServiceTest {
//
//    // Domain services
//    @Mock
//    private DepositRequestService depositRequestService;
//
//    @Mock
//    private WithdrawRequestService withdrawRequestService;
//
//    @Mock
//    private RefundRequestService refundRequestService;
//
//    @Mock
//    private OrderPaymentService orderPaymentService;
//
//    // Ports
//    @Mock
//    private CustomerPort customerPort;
//
//    @Mock
//    private WalletPort walletPort;
//
//    @Mock
//    private UserPort userPort;
//
//    @Mock
//    private OrderPort orderPort;
//
//    @Mock
//    private OrderItemPort orderItemPort;
//
//    @Mock
//    private ExchangePort exchangePort;
//
//    // Repositories
//    @Mock
//    private DepositRequestRepository depositRequestRepository;
//
//    @Mock
//    private WithdrawRequestRepository withdrawRequestRepository;
//
//    @Mock
//    private RefundRequestRepository refundRequestRepository;
//
//    @Mock
//    private OrderPaymentRepository orderPaymentRepository;
//
//    // Utils
//    @Mock
//    private PaymentMapper paymentMapper;
//
//    @Mock
//    private PriceCalculationService priceCalculationService;
//
//    // Domain objects
//    @Mock
//    private Customer customer;
//
//    @Mock
//    private WalletJpaEntity wallet;
//
//    @Mock
//    private User user;
//
//    @Mock
//    private OrderJpaEntity order;
//
//    @Mock
//    private OrderItem orderItem;
//
//    @Mock
//    private DepositRequestJpaEntity depositRequest;
//
//    @Mock
//    private WithdrawRequestJpaEntity withdrawRequest;
//
//    @Mock
//    private RefundRequestJpaEntity refundRequest;
//
//    @Mock
//    private OrderPaymentJpaEntity orderPayment;
//
//    // DTOs
//    @Mock
//    private DepositRequestDto depositRequestDto;
//
//    @Mock
//    private WithdrawRequestDto withdrawRequestDto;
//
//    @Mock
//    private RefundRequestDto refundRequestDto;
//
//    @Mock
//    private OrderPaymentRequestDto orderPaymentRequestDto;
//
//    @Mock
//    private DepositRequestResponseDto depositRequestResponseDto;
//
//    @Mock
//    private WithdrawResponseDto withdrawResponseDto;
//
//    @Mock
//    private RefundResponseDto refundResponseDto;
//
//    @Mock
//    private OrderPaymentResponseDto orderPaymentResponseDto;
//
//    @Mock
//    private WalletResponseDto walletResponseDto;
//
//    private PaymentApplicationService paymentApplicationService;
//
//    @BeforeEach
//    void setUp() {
//        paymentApplicationService = new PaymentApplicationService(
//                depositRequestService, withdrawRequestService, refundRequestService, orderPaymentService,
//                customerPort, walletPort, userPort, orderPort, orderItemPort, exchangePort,
//                depositRequestRepository, withdrawRequestRepository, refundRequestRepository, orderPaymentRepository,
//                paymentMapper, priceCalculationService
//        );
//    }
//
//    // ===== DEPOSIT TESTS =====
//
//    @Test
//    void processDepositRequest_ShouldCreateSuccessfully_WhenValidInput() {
//        // Given
//        UUID userUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID walletUuid = UUID.randomUUID();
//        UUID requestUuid = UUID.randomUUID();
//        BigDecimal originalAmount = new BigDecimal("1000.00");
//        BigDecimal exchangeRate = new BigDecimal("150.00");
//        Currency originalCurrency = Currency.USD;
//        String depositorName = "John Doe";
//        String bankAccount = "1234567890";
//
//        when(depositRequestDto.getAmountOriginal()).thenReturn(originalAmount);
//        when(depositRequestDto.getOriginalCurrency()).thenReturn(originalCurrency);
//        when(depositRequestDto.getDepositorName()).thenReturn(depositorName);
//        when(depositRequestDto.getBankAccount()).thenReturn(bankAccount);
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//        when(depositRequest.getRequestUuid()).thenReturn(requestUuid);
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(exchangePort.exchangeRate(originalCurrency, Currency.JPY)).thenReturn(exchangeRate);
//        when(depositRequestService.createDepositRequest(
//                customer, wallet, originalAmount, originalCurrency, exchangeRate, depositorName, bankAccount
//        )).thenReturn(depositRequest);
//        when(depositRequestRepository.save(depositRequest)).thenReturn(depositRequest);
//        when(paymentMapper.createDepositResponse(depositRequest)).thenReturn(depositRequestResponseDto);
//
//        // When
//        DepositRequestResponseDto result = paymentApplicationService.processDepositRequest(userUuid, depositRequestDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(depositRequestResponseDto, result);
//        verify(customerPort).findByUserUuid(userUuid);
//        verify(walletPort).findByCustomerUuid(customerUuid);
//        verify(exchangePort).exchangeRate(originalCurrency, Currency.JPY);
//        verify(depositRequestService).createDepositRequest(
//                customer, wallet, originalAmount, originalCurrency, exchangeRate, depositorName, bankAccount
//        );
//        verify(depositRequestRepository).save(depositRequest);
//        verify(paymentMapper).createDepositResponse(depositRequest);
//    }
//
//    @Test
//    void approveDepositRequest_ShouldApproveSuccessfully_WhenValidInput() {
//        // Given
//        UUID requestUuid = UUID.randomUUID();
//        UUID adminUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID walletUuid = UUID.randomUUID();
//        String memo = "Approved by admin";
//        BigDecimal convertedAmount = new BigDecimal("150000.00");
//
//        when(depositRequest.getCustomerUuid()).thenReturn(customerUuid);
//        when(depositRequest.getWalletUuid()).thenReturn(walletUuid);
//        when(depositRequest.getAmountConverted()).thenReturn(convertedAmount);
//
//        when(depositRequestRepository.findByRefundUuid(requestUuid)).thenReturn(Optional.of(depositRequest));
//        when(depositRequestService.approveDepositRequest(depositRequest, adminUuid, memo)).thenReturn(depositRequest);
//        when(walletPort.deposit(customerUuid, walletUuid, convertedAmount)).thenReturn(walletResponseDto);
//        when(depositRequestRepository.save(depositRequest)).thenReturn(depositRequest);
//        when(paymentMapper.approveDepositResponse(depositRequest, walletResponseDto)).thenReturn(depositRequestResponseDto);
//
//        // When
//        DepositRequestResponseDto result = paymentApplicationService.approveDepositRequest(requestUuid, adminUuid, memo);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(depositRequestResponseDto, result);
//        verify(depositRequestService).approveDepositRequest(depositRequest, adminUuid, memo);
//        verify(walletPort).deposit(customerUuid, walletUuid, convertedAmount);
//        verify(depositRequestRepository).save(depositRequest);
//        verify(paymentMapper).approveDepositResponse(depositRequest, walletResponseDto);
//    }
//
//    @Test
//    void approveDepositRequest_ShouldThrowException_WhenInsufficientBalance() {
//        // Given
//        UUID requestUuid = UUID.randomUUID();
//        UUID adminUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID walletUuid = UUID.randomUUID();
//        String memo = "Approved by admin";
//        BigDecimal convertedAmount = new BigDecimal("150000.00");
//
//        when(depositRequest.getCustomerUuid()).thenReturn(customerUuid);
//        when(depositRequest.getWalletUuid()).thenReturn(walletUuid);
//        when(depositRequest.getAmountConverted()).thenReturn(convertedAmount);
//
//        when(depositRequestRepository.findByRefundUuid(requestUuid)).thenReturn(Optional.of(depositRequest));
//        when(depositRequestService.approveDepositRequest(depositRequest, adminUuid, memo)).thenReturn(depositRequest);
//        when(walletPort.deposit(customerUuid, walletUuid, convertedAmount))
//                .thenThrow(new InsufficientBalanceException("Insufficient balance"));
//
//        // When & Then
//        assertThrows(DepositApprovalException.class, () -> {
//            paymentApplicationService.approveDepositRequest(requestUuid, adminUuid, memo);
//        });
//
//        verify(depositRequestService).markAsFailed(eq(depositRequest), contains("잔액 부족"));
//    }
//
//    @Test
//    void getDepositRequestInfoByAdmin_ShouldReturnInfo_WhenValidInput() {
//        // Given
//        UUID requestUuid = UUID.randomUUID();
//        UUID userUuid = UUID.randomUUID();
//
//        when(depositRequestRepository.findByRefundUuid(requestUuid)).thenReturn(Optional.of(depositRequest));
//        when(userPort.findUserByUserUuid(userUuid)).thenReturn(user);
//        when(depositRequestService.getDepositInfoByAdmin(depositRequest, user)).thenReturn(depositRequest);
//        when(paymentMapper.depositInfoResponse(depositRequest)).thenReturn(depositRequestResponseDto);
//
//        // When
//        DepositRequestResponseDto result = paymentApplicationService.getDepositRequestInfoByAdmin(requestUuid, userUuid);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(depositRequestResponseDto, result);
//        verify(userPort).findUserByUserUuid(userUuid);
//        verify(depositRequestService).getDepositInfoByAdmin(depositRequest, user);
//        verify(paymentMapper).depositInfoResponse(depositRequest);
//    }
//
//    @Test
//    void expireOldRequests_ShouldExpireRequests_WhenPendingRequestsExist() {
//        // Given
//        List<DepositRequestJpaEntity> pendingRequests = Arrays.asList(depositRequest);
//        List<DepositRequestJpaEntity> expiredRequests = Arrays.asList(depositRequest);
//
//        when(depositRequestRepository.findByStatus(DepositStatus.PENDING_ADMIN_APPROVAL))
//                .thenReturn(pendingRequests);
//        when(depositRequestService.expireOldRequests(pendingRequests)).thenReturn(expiredRequests);
//        when(depositRequestRepository.saveAll(expiredRequests)).thenReturn(expiredRequests);
//        when(paymentMapper.depositInfoResponse(depositRequest)).thenReturn(depositRequestResponseDto);
//
//        // When
//        List<DepositRequestResponseDto> result = paymentApplicationService.expireOldRequests();
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(depositRequestRepository).findByStatus(DepositStatus.PENDING_ADMIN_APPROVAL);
//        verify(depositRequestService).expireOldRequests(pendingRequests);
//        verify(depositRequestRepository).saveAll(expiredRequests);
//    }
//
//    // ===== WITHDRAW TESTS =====
//
//    @Test
//    void processWithdrawRequest_ShouldCreateSuccessfully_WhenValidInput() {
//        // Given
//        UUID userUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID requestUuid = UUID.randomUUID();
//        BigDecimal requestAmount = new BigDecimal("1000.00");
//        BigDecimal withdrawFee = new BigDecimal("300.00");
//        BigDecimal convertedAmount = new BigDecimal("850.00");
//        Currency targetCurrency = Currency.USD;
//        String bankName = "Test Bank";
//        String accountHolder = "John Doe";
//
//        when(withdrawRequestDto.getRequestAmount()).thenReturn(requestAmount);
//        when(withdrawRequestDto.getTargetCurrency()).thenReturn(targetCurrency);
//        when(withdrawRequestDto.getBankName()).thenReturn(bankName);
//        when(withdrawRequestDto.getAccountHolder()).thenReturn(accountHolder);
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//        when(withdrawRequest.getRequestUuid()).thenReturn(requestUuid);
//
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(priceCalculationService.convertAndCalculateTotalInLocalCurrency(requestAmount, targetCurrency))
//                .thenReturn(convertedAmount);
//        when(withdrawRequestService.createWithdrawRequest(
//                customer, wallet, requestAmount, targetCurrency, bankName, accountHolder, withdrawFee, convertedAmount
//        )).thenReturn(withdrawRequest);
//        when(withdrawRequestRepository.save(withdrawRequest)).thenReturn(withdrawRequest);
//        when(paymentMapper.createWithdrawResponse(withdrawRequest)).thenReturn(withdrawResponseDto);
//
//        // When
//        WithdrawResponseDto result = paymentApplicationService.processWithdrawRequest(userUuid, withdrawRequestDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(withdrawResponseDto, result);
//        verify(customerPort).findByUserUuid(userUuid);
//        verify(walletPort).findByCustomerUuid(customerUuid);
//        verify(priceCalculationService).convertAndCalculateTotalInLocalCurrency(requestAmount, targetCurrency);
//        verify(withdrawRequestService).createWithdrawRequest(
//                customer, wallet, requestAmount, targetCurrency, bankName, accountHolder, withdrawFee, convertedAmount
//        );
//        verify(withdrawRequestRepository).save(withdrawRequest);
//        verify(paymentMapper).createWithdrawResponse(withdrawRequest);
//
//    }
//
//    @Test
//    void approveWithdrawRequest_ShouldApproveSuccessfully_WhenValidInput() {
//        // Given
//        UUID requestUuid = UUID.randomUUID();
//        UUID adminUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID walletUuid = UUID.randomUUID();
//        String memo = "Approved by admin";
//        BigDecimal exchangeRate = new BigDecimal("150.00");
//        BigDecimal totalDeductAmount = new BigDecimal("1010.00");
//        BigDecimal convertedAmount = new BigDecimal("850.00");
//        Currency targetCurrency = Currency.USD;
//
//        when(withdrawRequest.getTargetCurrency()).thenReturn(targetCurrency);
//        when(withdrawRequest.getCustomerUuid()).thenReturn(customerUuid);
//        when(withdrawRequest.getWalletUuid()).thenReturn(walletUuid);
//        when(withdrawRequest.getTotalDeductAmount()).thenReturn(totalDeductAmount);
//        when(withdrawRequest.getConvertedAmount()).thenReturn(convertedAmount);
//
//        when(withdrawRequestRepository.findByRequestUuid(requestUuid)).thenReturn(Optional.of(withdrawRequest));
//        when(exchangePort.exchangeRate(Currency.JPY, targetCurrency)).thenReturn(exchangeRate);
//        when(withdrawRequestService.approveWithdrawRequest(withdrawRequest, adminUuid, memo, exchangeRate))
//                .thenReturn(withdrawRequest);
//        when(walletPort.withdrawal(customerUuid, walletUuid, totalDeductAmount)).thenReturn(walletResponseDto);
//        when(paymentMapper.approvedWithdrawResponse(withdrawRequest, walletResponseDto)).thenReturn(withdrawResponseDto);
//
//        // When
//        WithdrawResponseDto result = paymentApplicationService.approveWithdrawRequest(requestUuid, adminUuid, memo);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(withdrawResponseDto, result);
//        verify(exchangePort).exchangeRate(Currency.JPY, targetCurrency);
//        verify(withdrawRequestService).approveWithdrawRequest(withdrawRequest, adminUuid, memo, exchangeRate);
//        verify(walletPort).withdrawal(customerUuid, walletUuid, totalDeductAmount);
//        verify(paymentMapper).approvedWithdrawResponse(withdrawRequest, walletResponseDto);
//    }
//
//    // ===== REFUND TESTS =====
//
//    @Test
//    void processRefundRequest_ShouldCreateSuccessfully_WhenValidInput() {
//        // Given
//        UUID adminUuid = UUID.randomUUID();
//        UUID orderItemUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID refundUuid = UUID.randomUUID();
//        BigDecimal priceOriginal = new BigDecimal("500.00");
//        String refundReason = "Product defect";
//        RefundType refundType = RefundType.ADMIN_DECISION;
//
//        when(refundRequestDto.getRefundReason()).thenReturn(refundReason);
//        when(refundRequestDto.getRefundType()).thenReturn(refundType);
//        when(orderItem.getCustomerUuid()).thenReturn(customerUuid);
//        when(orderItem.getPriceOriginal()).thenReturn(priceOriginal);
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//        when(refundRequest.getRefundUuid()).thenReturn(refundUuid);
//
//        when(orderItemPort.findByOrderItemUuid(orderItemUuid)).thenReturn(orderItem);
//        when(customerPort.findByCustomerUuid(customerUuid)).thenReturn(customer);
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(refundRequestService.createRefundRequest(
//                customer, wallet, orderItem, priceOriginal, adminUuid, refundReason, refundType
//        )).thenReturn(refundRequest);
//        when(refundRequestRepository.save(refundRequest)).thenReturn(refundRequest);
//        when(paymentMapper.createRefundResponse(refundRequest)).thenReturn(refundResponseDto);
//
//        // When
//        RefundResponseDto result = paymentApplicationService.processRefundRequest(adminUuid, orderItemUuid, refundRequestDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(refundResponseDto, result);
//        verify(orderItemPort).findByOrderItemUuid(orderItemUuid);
//        verify(orderItem).isCancel();
//        verify(customerPort).findByCustomerUuid(customerUuid);
//        verify(walletPort).findByCustomerUuid(customerUuid);
//        verify(refundRequestService).createRefundRequest(
//                customer, wallet, orderItem, priceOriginal, adminUuid, refundReason, refundType
//        );
//        verify(refundRequestRepository).save(refundRequest);
//        verify(paymentMapper).createRefundResponse(refundRequest);
//    }
//
//    // ===== ORDER PAYMENT TESTS =====
//
//    @Test
//    void createFirstPayment_ShouldCreateSuccessfully_WhenValidInput() {
//        // Given
//        UUID adminUuid = UUID.randomUUID();
//        UUID orderUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        BigDecimal finalPriceOriginal = new BigDecimal("1000.00");
//
//        when(order.getCustomer()).thenReturn(customer);
//        when(order.getFinalPriceOriginal()).thenReturn(finalPriceOriginal);
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//
//        when(userPort.findUserByUserUuid(adminUuid)).thenReturn(user);
//        when(orderPort.findOrderByOrderUuid(orderUuid)).thenReturn(order);
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(orderPaymentService.crateOrderPayment(customer, wallet, order, finalPriceOriginal, user))
//                .thenReturn(orderPayment);
//        when(orderPaymentRepository.save(orderPayment)).thenReturn(orderPayment);
//        when(paymentMapper.createOrderPayment(orderPayment)).thenReturn(orderPaymentResponseDto);
//
//        // When
//        OrderPaymentResponseDto result = paymentApplicationService.createFirstPayment(adminUuid, orderUuid);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPaymentResponseDto, result);
//        verify(userPort).findUserByUserUuid(adminUuid);
//        verify(orderPort).findOrderByOrderUuid(orderUuid);
//        verify(walletPort).findByCustomerUuid(customerUuid);
//        verify(orderPaymentService).crateOrderPayment(customer, wallet, order, finalPriceOriginal, user);
//        verify(orderPaymentRepository).save(orderPayment);
//        verify(paymentMapper).createOrderPayment(orderPayment);
//    }
//
//    @Test
//    void completeFirstPayment_ShouldCompleteSuccessfully_WhenValidInput() {
//        // Given
//        UUID userUuid = UUID.randomUUID();
//        UUID paymentUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID walletUuid = UUID.randomUUID();
//        UUID orderUuid = UUID.randomUUID();
//        BigDecimal paymentAmount = new BigDecimal("1000.00");
//
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//        when(wallet.getWalletUuid()).thenReturn(walletUuid);
//        when(orderPayment.getPaymentAmount()).thenReturn(paymentAmount);
//        when(orderPayment.getOrderUuid()).thenReturn(orderUuid);
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(orderPaymentRepository.findByCustomerUuidAndPaymentUuid(customerUuid, paymentUuid))
//                .thenReturn(Optional.of(orderPayment));
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(walletPort.deposit(customerUuid, walletUuid, paymentAmount)).thenReturn(walletResponseDto);
//        when(orderPaymentService.completePayment(orderPayment)).thenReturn(orderPayment);
//        when(orderPort.findOrderByOrderUuid(orderUuid)).thenReturn(order);
//        when(orderPaymentRepository.save(orderPayment)).thenReturn(orderPayment);
//        when(paymentMapper.completeOrderPayment(orderPayment, walletResponseDto)).thenReturn(orderPaymentResponseDto);
//
//        // When
//        OrderPaymentResponseDto result = paymentApplicationService.completeFirstPayment(userUuid, paymentUuid);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPaymentResponseDto, result);
//        verify(customerPort).findByUserUuid(userUuid);
//        verify(walletPort).deposit(customerUuid, walletUuid, paymentAmount);
//        verify(orderPaymentService).completePayment(orderPayment);
//        verify(order).fistOrderPayment();
//        verify(orderPaymentRepository).save(orderPayment);
//        verify(paymentMapper).completeOrderPayment(orderPayment, walletResponseDto);
//    }
//
//    @Test
//    void completeFirstPayment_ShouldThrowException_WhenInsufficientBalance() {
//        // Given
//        UUID userUuid = UUID.randomUUID();
//        UUID paymentUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID walletUuid = UUID.randomUUID();
//        BigDecimal paymentAmount = new BigDecimal("1000.00");
//
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//        when(wallet.getWalletUuid()).thenReturn(walletUuid);
//        when(orderPayment.getPaymentAmount()).thenReturn(paymentAmount);
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(orderPaymentRepository.findByCustomerUuidAndPaymentUuid(customerUuid, paymentUuid))
//                .thenReturn(Optional.of(orderPayment));
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(walletPort.deposit(customerUuid, walletUuid, paymentAmount))
//                .thenThrow(new InsufficientBalanceException("Insufficient balance"));
//
//        // When & Then
//        assertThrows(OrderPaymentCompletionException.class, () -> {
//            paymentApplicationService.completeFirstPayment(userUuid, paymentUuid);
//        });
//
//        verify(orderPaymentService).markAsFailed(eq(orderPayment), contains("잔액 부족"));
//    }
//
//    // ===== HELPER METHOD TESTS =====
//
//    @Test
//    void findDepositRequestByRequestUuid_ShouldThrowException_WhenNotFound() {
//        // Given
//        UUID requestUuid = UUID.randomUUID();
//        when(depositRequestRepository.findByRefundUuid(requestUuid)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(DepositNotFoundException.class, () -> {
//            paymentApplicationService.approveDepositRequest(requestUuid, UUID.randomUUID(), "memo");
//        });
//    }
//
//    @Test
//    void findWithdrawRequestByRequestUuid_ShouldThrowException_WhenNotFound() {
//        // Given
//        UUID requestUuid = UUID.randomUUID();
//        when(withdrawRequestRepository.findByRequestUuid(requestUuid)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(WithdrawRequestNotFoundException.class, () -> {
//            paymentApplicationService.approveWithdrawRequest(requestUuid, UUID.randomUUID(), "memo");
//        });
//    }
//
//    @Test
//    void findRefundRequestByRefundUuid_ShouldThrowException_WhenNotFound() {
//        // Given
//        UUID refundUuid = UUID.randomUUID();
//        when(refundRequestRepository.findByRefundUuid(refundUuid)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(RefundNotFoundException.class, () -> {
//            paymentApplicationService.approveRefundRequest(refundUuid, UUID.randomUUID(), "memo");
//        });
//    }
//
//    @Test
//    void findOrderPaymentByCustomerUuidAndPaymentUuid_ShouldThrowException_WhenNotFound() {
//        // Given
//        UUID userUuid = UUID.randomUUID();
//        UUID paymentUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(orderPaymentRepository.findByCustomerUuidAndPaymentUuid(customerUuid, paymentUuid))
//                .thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(OrderPaymentNotFoundException.class, () -> {
//            paymentApplicationService.completeFirstPayment(userUuid, paymentUuid);
//        });
//    }
//
//    // ===== INTEGRATION WORKFLOW TESTS =====
//
//    @Test
//    void completeDepositWorkflow_ShouldExecuteSuccessfully() {
//        // Given
//        UUID userUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID requestUuid = UUID.randomUUID();
//        UUID adminUuid = UUID.randomUUID();
//        String memo = "Deposit approved";
//        BigDecimal originalAmount = new BigDecimal("1000.00");
//        BigDecimal exchangeRate = new BigDecimal("150.00");
//        BigDecimal convertedAmount = new BigDecimal("150000.00");
//        Currency originalCurrency = Currency.USD;
//
//        // Setup mocks for process deposit request
//        when(depositRequestDto.getAmountOriginal()).thenReturn(originalAmount);
//        when(depositRequestDto.getOriginalCurrency()).thenReturn(originalCurrency);
//        when(depositRequestDto.getDepositorName()).thenReturn("John Doe");
//        when(depositRequestDto.getBankAccount()).thenReturn("1234567890");
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//        when(depositRequest.getRequestUuid()).thenReturn(requestUuid);
//        when(depositRequest.getCustomerUuid()).thenReturn(customerUuid);
//        when(depositRequest.getWalletUuid()).thenReturn(UUID.randomUUID());
//        when(depositRequest.getAmountConverted()).thenReturn(convertedAmount);
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(exchangePort.exchangeRate(originalCurrency, Currency.JPY)).thenReturn(exchangeRate);
//        when(depositRequestService.createDepositRequest(any(), any(), any(), any(), any(), any(), any()))
//                .thenReturn(depositRequest);
//        when(depositRequestRepository.save(depositRequest)).thenReturn(depositRequest);
//        when(paymentMapper.createDepositResponse(depositRequest)).thenReturn(depositRequestResponseDto);
//
//        // Setup mocks for approve deposit request
//        when(depositRequestRepository.findByRefundUuid(requestUuid)).thenReturn(Optional.of(depositRequest));
//        when(depositRequestService.approveDepositRequest(depositRequest, adminUuid, memo)).thenReturn(depositRequest);
//        when(walletPort.deposit(any(), any(), any())).thenReturn(walletResponseDto);
//        when(paymentMapper.approveDepositResponse(depositRequest, walletResponseDto)).thenReturn(depositRequestResponseDto);
//
//        // When - Process deposit request
//        DepositRequestResponseDto processResult = paymentApplicationService.processDepositRequest(userUuid, depositRequestDto);
//
//        // When - Approve deposit request
//        DepositRequestResponseDto approveResult = paymentApplicationService.approveDepositRequest(requestUuid, adminUuid, memo);
//
//        // Then
//        assertNotNull(processResult);
//        assertNotNull(approveResult);
//        assertEquals(depositRequestResponseDto, processResult);
//        assertEquals(depositRequestResponseDto, approveResult);
//
//        // Verify the complete workflow
//        verify(customerPort).findByUserUuid(userUuid);
//        verify(walletPort).findByCustomerUuid(customerUuid);
//        verify(exchangePort).exchangeRate(originalCurrency, Currency.JPY);
//        verify(depositRequestService).createDepositRequest(any(), any(), any(), any(), any(), any(), any());
//        verify(depositRequestService).approveDepositRequest(depositRequest, adminUuid, memo);
//        verify(walletPort).deposit(any(), any(), any());
//        verify(depositRequestRepository, times(2)).save(depositRequest);
//    }
//
//    // ===== EDGE CASE TESTS =====
//
//    @Test
//    void processDepositRequest_ShouldWork_WithZeroAmount() {
//        // Given
//        UUID userUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        BigDecimal zeroAmount = BigDecimal.ZERO;
//        BigDecimal exchangeRate = new BigDecimal("150.00");
//        Currency originalCurrency = Currency.USD;
//
//        when(depositRequestDto.getAmountOriginal()).thenReturn(zeroAmount);
//        when(depositRequestDto.getOriginalCurrency()).thenReturn(originalCurrency);
//        when(depositRequestDto.getDepositorName()).thenReturn("John Doe");
//        when(depositRequestDto.getBankAccount()).thenReturn("1234567890");
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(exchangePort.exchangeRate(originalCurrency, Currency.JPY)).thenReturn(exchangeRate);
//        when(depositRequestService.createDepositRequest(any(), any(), eq(zeroAmount), any(), any(), any(), any()))
//                .thenReturn(depositRequest);
//        when(depositRequestRepository.save(depositRequest)).thenReturn(depositRequest);
//        when(paymentMapper.createDepositResponse(depositRequest)).thenReturn(depositRequestResponseDto);
//
//        // When
//        DepositRequestResponseDto result = paymentApplicationService.processDepositRequest(userUuid, depositRequestDto);
//
//        // Then
//        assertNotNull(result);
//        verify(depositRequestService).createDepositRequest(any(), any(), eq(zeroAmount), any(), any(), any(), any());
//    }
//
//    @Test
//    void processWithdrawRequest_ShouldWork_WithLargeAmount() {
//        // Given
//        UUID userUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        BigDecimal largeAmount = new BigDecimal("999999999.99");
//        BigDecimal withdrawFee = new BigDecimal("10.00");
//        BigDecimal convertedAmount = new BigDecimal("850000000.00");
//        Currency targetCurrency = Currency.USD;
//
//        when(withdrawRequestDto.getRequestAmount()).thenReturn(largeAmount);
//        when(withdrawRequestDto.getTargetCurrency()).thenReturn(targetCurrency);
//        when(withdrawRequestDto.getBankName()).thenReturn("Large Bank");
//        when(withdrawRequestDto.getAccountHolder()).thenReturn("Big Spender");
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(priceCalculationService.convertAndCalculateTotalInLocalCurrency(largeAmount, targetCurrency))
//                .thenReturn(convertedAmount);
//        when(withdrawRequestService.createWithdrawRequest(any(), any(), eq(largeAmount), any(), any(), any(), any(), any()))
//                .thenReturn(withdrawRequest);
//        when(withdrawRequestRepository.save(withdrawRequest)).thenReturn(withdrawRequest);
//        when(paymentMapper.createWithdrawResponse(withdrawRequest)).thenReturn(withdrawResponseDto);
//
//        // When
//        WithdrawResponseDto result = paymentApplicationService.processWithdrawRequest(userUuid, withdrawRequestDto);
//
//        // Then
//        assertNotNull(result);
//        verify(withdrawRequestService).createWithdrawRequest(any(), any(), eq(largeAmount), any(), any(), any(), any(), any());
//
//    }
//
//    @Test
//    void approveDepositRequest_ShouldHandleWalletNotActiveException() {
//        // Given
//        UUID requestUuid = UUID.randomUUID();
//        UUID adminUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID walletUuid = UUID.randomUUID();
//        String memo = "Approved by admin";
//        BigDecimal convertedAmount = new BigDecimal("150000.00");
//
//        when(depositRequest.getCustomerUuid()).thenReturn(customerUuid);
//        when(depositRequest.getWalletUuid()).thenReturn(walletUuid);
//        when(depositRequest.getAmountConverted()).thenReturn(convertedAmount);
//
//        when(depositRequestRepository.findByRefundUuid(requestUuid)).thenReturn(Optional.of(depositRequest));
//        when(depositRequestService.approveDepositRequest(depositRequest, adminUuid, memo)).thenReturn(depositRequest);
//        when(walletPort.deposit(customerUuid, walletUuid, convertedAmount))
//                .thenThrow(new WalletNotActiveException("WalletJpaEntity not active"));
//
//        // When & Then
//        assertThrows(DepositApprovalException.class, () -> {
//            paymentApplicationService.approveDepositRequest(requestUuid, adminUuid, memo);
//        });
//
//        verify(depositRequestService).markAsFailed(eq(depositRequest), contains("비활성 지갑"));
//    }
//
//    @Test
//    void approveWithdrawRequest_ShouldHandleInsufficientBalance() {
//        // Given
//        UUID requestUuid = UUID.randomUUID();
//        UUID adminUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID walletUuid = UUID.randomUUID();
//        String memo = "Approved by admin";
//        BigDecimal exchangeRate = new BigDecimal("150.00");
//        BigDecimal totalDeductAmount = new BigDecimal("1010.00");
//        Currency targetCurrency = Currency.USD;
//
//        when(withdrawRequest.getTargetCurrency()).thenReturn(targetCurrency);
//        when(withdrawRequest.getCustomerUuid()).thenReturn(customerUuid);
//        when(withdrawRequest.getWalletUuid()).thenReturn(walletUuid);
//        when(withdrawRequest.getTotalDeductAmount()).thenReturn(totalDeductAmount);
//
//        when(withdrawRequestRepository.findByRequestUuid(requestUuid)).thenReturn(Optional.of(withdrawRequest));
//        when(exchangePort.exchangeRate(Currency.JPY, targetCurrency)).thenReturn(exchangeRate);
//        when(withdrawRequestService.approveWithdrawRequest(withdrawRequest, adminUuid, memo, exchangeRate))
//                .thenReturn(withdrawRequest);
//        when(walletPort.withdrawal(customerUuid, walletUuid, totalDeductAmount))
//                .thenThrow(new InsufficientBalanceException("Insufficient balance"));
//
//        // When & Then
//        assertThrows(WithdrawApprovalException.class, () -> {
//            paymentApplicationService.approveWithdrawRequest(requestUuid, adminUuid, memo);
//        });
//
//        verify(withdrawRequestService).markAsFailed(eq(withdrawRequest), contains("잔액 부족"));
//    }
//
//    @Test
//    void processRefundRequest_ShouldWork_WithNullOptionalFields() {
//        // Given
//        UUID adminUuid = UUID.randomUUID();
//        UUID orderItemUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        BigDecimal priceOriginal = new BigDecimal("500.00");
//        RefundType refundType = RefundType.ADMIN_DECISION;
//
//        when(refundRequestDto.getRefundReason()).thenReturn(null);
//        when(refundRequestDto.getRefundType()).thenReturn(refundType);
//        when(orderItem.getCustomerUuid()).thenReturn(customerUuid);
//        when(orderItem.getPriceOriginal()).thenReturn(priceOriginal);
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//
//        when(orderItemPort.findByOrderItemUuid(orderItemUuid)).thenReturn(orderItem);
//        when(customerPort.findByCustomerUuid(customerUuid)).thenReturn(customer);
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(refundRequestService.createRefundRequest(
//                customer, wallet, orderItem, priceOriginal, adminUuid, null, refundType
//        )).thenReturn(refundRequest);
//        when(refundRequestRepository.save(refundRequest)).thenReturn(refundRequest);
//        when(paymentMapper.createRefundResponse(refundRequest)).thenReturn(refundResponseDto);
//
//        // When
//        RefundResponseDto result = paymentApplicationService.processRefundRequest(adminUuid, orderItemUuid, refundRequestDto);
//
//        // Then
//        assertNotNull(result);
//        verify(refundRequestService).createRefundRequest(
//                customer, wallet, orderItem, priceOriginal, adminUuid, null, refundType
//        );
//    }
//
//    @Test
//    void approveRefundRequest_ShouldHandleOptimisticLockException() {
//        // Given
//        UUID refundUuid = UUID.randomUUID();
//        UUID adminUuid = UUID.randomUUID();
//        String memo = "Approved";
//
//        when(refundRequestRepository.findByRefundUuid(refundUuid)).thenReturn(Optional.of(refundRequest));
//        when(userPort.findUserByUserUuid(adminUuid)).thenReturn(user);
//        when(refundRequestService.processRefundRequest(refundRequest, user, memo))
//                .thenThrow(new OptimisticLockException("Optimistic lock exception"));
//
//        // When & Then
//        assertThrows(ConcurrentModificationException.class, () -> {
//            paymentApplicationService.approveRefundRequest(refundUuid, adminUuid, memo);
//        });
//    }
//
//    @Test
//    void completeSecondPayment_ShouldCompleteSuccessfully_WhenValidInput() {
//        // Given
//        UUID userUuid = UUID.randomUUID();
//        UUID paymentUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        UUID walletUuid = UUID.randomUUID();
//        UUID orderUuid = UUID.randomUUID();
//        BigDecimal paymentAmount = new BigDecimal("50.00");
//
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//        when(wallet.getWalletUuid()).thenReturn(walletUuid);
//        when(orderPayment.getPaymentAmount()).thenReturn(paymentAmount);
//        when(orderPayment.getOrderUuid()).thenReturn(orderUuid);
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(orderPaymentRepository.findByCustomerUuidAndPaymentUuid(customerUuid, paymentUuid))
//                .thenReturn(Optional.of(orderPayment));
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(walletPort.deposit(customerUuid, walletUuid, paymentAmount)).thenReturn(walletResponseDto);
//        when(orderPaymentService.completeSecondOrderPayment(orderPayment)).thenReturn(orderPayment);
//        when(orderPort.findOrderByOrderUuid(orderUuid)).thenReturn(order);
//        when(orderPaymentRepository.save(orderPayment)).thenReturn(orderPayment);
//        when(paymentMapper.completeOrderPayment(orderPayment, walletResponseDto)).thenReturn(orderPaymentResponseDto);
//
//        // When
//        OrderPaymentResponseDto result = paymentApplicationService.completeSecondPayment(userUuid, paymentUuid);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPaymentResponseDto, result);
//        verify(orderPaymentService).completeSecondOrderPayment(orderPayment);
//        verify(order).secondOrderPayment();
//        verify(orderPaymentRepository).save(orderPayment);
//    }
//
//    @Test
//    void createSecondPayment_ShouldCreateSuccessfully_WhenValidInput() {
//        // Given
//        UUID adminUuid = UUID.randomUUID();
//        UUID orderUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//        BigDecimal deliveryFee = new BigDecimal("25.00");
//
//        when(orderPaymentRequestDto.getDeliveryFee()).thenReturn(deliveryFee);
//        when(order.getCustomer()).thenReturn(customer);
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//
//        when(userPort.findUserByUserUuid(adminUuid)).thenReturn(user);
//        when(orderPort.findOrderByOrderUuid(orderUuid)).thenReturn(order);
//        when(walletPort.findByCustomerUuid(customerUuid)).thenReturn(wallet);
//        when(orderPaymentService.createSecondOrderPayment(customer, wallet, order, deliveryFee, user))
//                .thenReturn(orderPayment);
//        when(paymentMapper.createOrderPayment(orderPayment)).thenReturn(orderPaymentResponseDto);
//
//        // When
//        OrderPaymentResponseDto result = paymentApplicationService.createSecondPayment(adminUuid, orderUuid, orderPaymentRequestDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPaymentResponseDto, result);
//        verify(orderPaymentService).createSecondOrderPayment(customer, wallet, order, deliveryFee, user);
//        verify(paymentMapper).createOrderPayment(orderPayment);
//    }
//
//    @Test
//    void getOrderPaymentInfoByAdmin_ShouldReturnInfo_WhenValidInput() {
//        // Given
//        UUID adminUuid = UUID.randomUUID();
//        UUID paymentUuid = UUID.randomUUID();
//
//        when(orderPaymentRepository.findByPaymentUuid(paymentUuid)).thenReturn(Optional.of(orderPayment));
//        when(userPort.findUserByUserUuid(adminUuid)).thenReturn(user);
//        when(orderPaymentService.getOrderPaymentInfoByAdmin(user, orderPayment)).thenReturn(orderPayment);
//        when(paymentMapper.orderPaymentInfo(orderPayment)).thenReturn(orderPaymentResponseDto);
//
//        // When
//        OrderPaymentResponseDto result = paymentApplicationService.getOrderPaymentInfoByAdmin(adminUuid, paymentUuid);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPaymentResponseDto, result);
//        verify(userPort).findUserByUserUuid(adminUuid);
//        verify(orderPaymentService).getOrderPaymentInfoByAdmin(user, orderPayment);
//        verify(paymentMapper).orderPaymentInfo(orderPayment);
//    }
//
//    @Test
//    void getOrderPaymentInfo_ShouldReturnInfo_WhenValidInput() {
//        // Given
//        UUID userUuid = UUID.randomUUID();
//        UUID paymentUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(orderPaymentRepository.findByCustomerUuidAndPaymentUuid(customerUuid, paymentUuid))
//                .thenReturn(Optional.of(orderPayment));
//        when(orderPaymentService.getOrderPaymentInfo(orderPayment)).thenReturn(orderPayment);
//        when(paymentMapper.orderPaymentInfo(orderPayment)).thenReturn(orderPaymentResponseDto);
//
//        // When
//        OrderPaymentResponseDto result = paymentApplicationService.getOrderPaymentInfo(userUuid, paymentUuid);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPaymentResponseDto, result);
//        verify(customerPort).findByUserUuid(userUuid);
//        verify(orderPaymentService).getOrderPaymentInfo(orderPayment);
//        verify(paymentMapper).orderPaymentInfo(orderPayment);
//    }
//
//    @Test
//    void getWithdrawInfo_ShouldReturnInfo_WhenValidInput() {
//        // Given
//        UUID requestUuid = UUID.randomUUID();
//        UUID userUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(withdrawRequestRepository.findByRequestUuidAndCustomerUuid(requestUuid, customerUuid))
//                .thenReturn(Optional.of(withdrawRequest));
//        when(withdrawRequestService.getWithdrawInfo(withdrawRequest)).thenReturn(withdrawRequest);
//        when(paymentMapper.withdrawInfoResponse(withdrawRequest)).thenReturn(withdrawResponseDto);
//
//        // When
//        WithdrawResponseDto result = paymentApplicationService.getWithdrawInfo(requestUuid, userUuid);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(withdrawResponseDto, result);
//        verify(customerPort).findByUserUuid(userUuid);
//        verify(withdrawRequestService).getWithdrawInfo(withdrawRequest);
//        verify(paymentMapper).withdrawInfoResponse(withdrawRequest);
//    }
//
//    @Test
//    void getRefundInfo_ShouldReturnInfo_WhenValidInput() {
//        // Given
//        UUID refundUuid = UUID.randomUUID();
//        UUID userUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(refundRequestRepository.findByRefundUuidAndCustomerUuid(refundUuid, customerUuid))
//                .thenReturn(Optional.of(refundRequest));
//        when(refundRequestService.getRefundInfo(refundRequest)).thenReturn(refundRequest);
//        when(paymentMapper.refundInfoResponse(refundRequest)).thenReturn(refundResponseDto);
//
//        // When
//        RefundResponseDto result = paymentApplicationService.getRefundInfo(refundUuid, userUuid);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(refundResponseDto, result);
//        verify(customerPort).findByUserUuid(userUuid);
//        verify(refundRequestService).getRefundInfo(refundRequest);
//        verify(paymentMapper).refundInfoResponse(refundRequest);
//    }
//
//    @Test
//    void getDepositRequestInfo_ShouldReturnInfo_WhenValidInput() {
//        // Given
//        UUID requestUuid = UUID.randomUUID();
//        UUID userUuid = UUID.randomUUID();
//        UUID customerUuid = UUID.randomUUID();
//
//        when(customer.getCustomerUuid()).thenReturn(customerUuid);
//
//        when(customerPort.findByUserUuid(userUuid)).thenReturn(customer);
//        when(depositRequestRepository.findByRefundUuidAndCustomerUuid(requestUuid, customerUuid))
//                .thenReturn(Optional.of(depositRequest));
//        when(depositRequestService.getDepositInfo(depositRequest, customer)).thenReturn(depositRequest);
//        when(paymentMapper.depositInfoResponse(depositRequest)).thenReturn(depositRequestResponseDto);
//
//        // When
//        DepositRequestResponseDto result = paymentApplicationService.getDepositRequestInfo(requestUuid, userUuid);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(depositRequestResponseDto, result);
//        verify(customerPort).findByUserUuid(userUuid);
//        verify(depositRequestService).getDepositInfo(depositRequest, customer);
//        verify(paymentMapper).depositInfoResponse(depositRequest);
//    }
//
//    @Test
//    void expireOldRequests_ShouldReturnEmptyList_WhenNoPendingRequests() {
//        // Given
//        when(depositRequestRepository.findByStatus(DepositStatus.PENDING_ADMIN_APPROVAL))
//                .thenReturn(Collections.emptyList());
//        when(depositRequestService.expireOldRequests(Collections.emptyList()))
//                .thenReturn(Collections.emptyList());
//        when(depositRequestRepository.saveAll(Collections.emptyList()))
//                .thenReturn(Collections.emptyList());
//
//        // When
//        List<DepositRequestResponseDto> result = paymentApplicationService.expireOldRequests();
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(depositRequestRepository).findByStatus(DepositStatus.PENDING_ADMIN_APPROVAL);
//        verify(depositRequestService).expireOldRequests(Collections.emptyList());
//    }
//}