//package com.kijinkai.domain.payment.domain.service;
//
//import com.kijinkai.domain.customer.adapter.out.persistence.entity.Customer;
//import com.kijinkai.domain.order.entity.Order;
//import com.kijinkai.domain.payment.domain.entity.OrderPayment;
//import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
//import com.kijinkai.domain.user.adapter.in.web.validator.UserValidator;
//import com.kijinkai.domain.wallet.entity.Wallet;
//import org.junit.jupiter.api.BeforeEach;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderPaymentServiceTest {
//    @Mock
//    private PaymentFactory paymentFactory;
//
//    @Mock
//    private UserValidator userValidator;
//
//    @Mock
//    private Customer customer;
//
//    @Mock
//    private Wallet wallet;
//
//    @Mock
//    private Order order;
//
//    @Mock
//    private OrderPayment orderPayment;
//
//    @Mock
//    private User admin;
//
//    private OrderPaymentService orderPaymentService;
//
//    @BeforeEach
//    void setUp() {
//        orderPaymentService = new OrderPaymentService(paymentFactory, userValidator);
//    }
//
//    // Tests for crateOrderPayment (note: typo in original method name)
//    @Test
//    void crateOrderPayment_ShouldCreateSuccessfully_WhenAdminHasValidRole() {
//        // Given
//        BigDecimal paymentAmount = new BigDecimal("1000.00");
//        UUID adminUuid = UUID.randomUUID();
//        when(admin.getUserUuid()).thenReturn(adminUuid);
//        doNothing().when(userValidator).requireAdminRole(admin);
//        when(paymentFactory.createOrderFirstPayment(customer, wallet, order, paymentAmount, adminUuid))
//                .thenReturn(orderPayment);
//
//        // When
//        OrderPayment result = orderPaymentService.crateOrderPayment(
//                customer, wallet, order, paymentAmount, admin
//        );
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(userValidator).requireAdminRole(admin);
//        verify(paymentFactory).createOrderFirstPayment(customer, wallet, order, paymentAmount, adminUuid);
//        verify(admin).getUserUuid();
//    }
//
//    @Test
//    void crateOrderPayment_ShouldThrowException_WhenUserIsNotAdmin() {
//        // Given
//        BigDecimal paymentAmount = new BigDecimal("1000.00");
//        doThrow(new SecurityException("Admin role required"))
//                .when(userValidator).requireAdminRole(admin);
//
//        // When & Then
//        assertThrows(SecurityException.class, () -> {
//            orderPaymentService.crateOrderPayment(customer, wallet, order, paymentAmount, admin);
//        });
//
//        verify(userValidator).requireAdminRole(admin);
//        verify(paymentFactory, never()).createOrderFirstPayment(any(), any(), any(), any(), any());
//        verify(admin, never()).getUserUuid();
//    }
//
//    @Test
//    void crateOrderPayment_ShouldThrowException_WhenFactoryFails() {
//        // Given
//        BigDecimal paymentAmount = new BigDecimal("1000.00");
//        UUID adminUuid = UUID.randomUUID();
//        when(admin.getUserUuid()).thenReturn(adminUuid);
//        doNothing().when(userValidator).requireAdminRole(admin);
//        when(paymentFactory.createOrderFirstPayment(customer, wallet, order, paymentAmount, adminUuid))
//                .thenThrow(new RuntimeException("Payment creation failed"));
//
//        // When & Then
//        assertThrows(RuntimeException.class, () -> {
//            orderPaymentService.crateOrderPayment(customer, wallet, order, paymentAmount, admin);
//        });
//
//        verify(userValidator).requireAdminRole(admin);
//        verify(paymentFactory).createOrderFirstPayment(customer, wallet, order, paymentAmount, adminUuid);
//        verify(admin).getUserUuid();
//    }
//
//    @Test
//    void crateOrderPayment_ShouldHandleZeroAmount() {
//        // Given
//        BigDecimal zeroAmount = BigDecimal.ZERO;
//        UUID adminUuid = UUID.randomUUID();
//        when(admin.getUserUuid()).thenReturn(adminUuid);
//        doNothing().when(userValidator).requireAdminRole(admin);
//        when(paymentFactory.createOrderFirstPayment(customer, wallet, order, zeroAmount, adminUuid))
//                .thenReturn(orderPayment);
//
//        // When
//        OrderPayment result = orderPaymentService.crateOrderPayment(
//                customer, wallet, order, zeroAmount, admin
//        );
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(userValidator).requireAdminRole(admin);
//        verify(paymentFactory).createOrderFirstPayment(customer, wallet, order, zeroAmount, adminUuid);
//    }
//
//    // Tests for completePayment
//    @Test
//    void completePayment_ShouldCompleteSuccessfully() {
//        // When
//        OrderPayment result = orderPaymentService.completePayment(orderPayment);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(orderPayment).complete();
//    }
//
//    @Test
//    void completePayment_ShouldThrowException_WhenCompleteFails() {
//        // Given
//        doThrow(new IllegalStateException("Cannot complete payment"))
//                .when(orderPayment).complete();
//
//        // When & Then
//        assertThrows(IllegalStateException.class, () -> {
//            orderPaymentService.completePayment(orderPayment);
//        });
//
//        verify(orderPayment).complete();
//    }
//
//    // Tests for createSecondOrderPayment
//    @Test
//    void createSecondOrderPayment_ShouldCreateSuccessfully_WhenAdminHasValidRole() {
//        // Given
//        BigDecimal paymentAmount = new BigDecimal("50.00");
//        UUID adminUuid = UUID.randomUUID();
//        when(admin.getUserUuid()).thenReturn(adminUuid);
//        doNothing().when(userValidator).requireAdminRole(admin);
//        when(paymentFactory.createOrderSecondPayment(customer, wallet, order, paymentAmount, adminUuid))
//                .thenReturn(orderPayment);
//
//        // When
//        OrderPayment result = orderPaymentService.createSecondOrderPayment(
//                customer, wallet, order, paymentAmount, admin
//        );
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(userValidator).requireAdminRole(admin);
//        verify(paymentFactory).createOrderSecondPayment(customer, wallet, order, paymentAmount, adminUuid);
//        verify(admin).getUserUuid();
//    }
//
//    @Test
//    void createSecondOrderPayment_ShouldThrowException_WhenUserIsNotAdmin() {
//        // Given
//        BigDecimal paymentAmount = new BigDecimal("50.00");
//        doThrow(new SecurityException("Admin role required"))
//                .when(userValidator).requireAdminRole(admin);
//
//        // When & Then
//        assertThrows(SecurityException.class, () -> {
//            orderPaymentService.createSecondOrderPayment(customer, wallet, order, paymentAmount, admin);
//        });
//
//        verify(userValidator).requireAdminRole(admin);
//        verify(paymentFactory, never()).createOrderSecondPayment(any(), any(), any(), any(), any());
//        verify(admin, never()).getUserUuid();
//    }
//
//    @Test
//    void createSecondOrderPayment_ShouldThrowException_WhenFactoryFails() {
//        // Given
//        BigDecimal paymentAmount = new BigDecimal("50.00");
//        UUID adminUuid = UUID.randomUUID();
//        when(admin.getUserUuid()).thenReturn(adminUuid);
//        doNothing().when(userValidator).requireAdminRole(admin);
//        when(paymentFactory.createOrderSecondPayment(customer, wallet, order, paymentAmount, adminUuid))
//                .thenThrow(new RuntimeException("Second payment creation failed"));
//
//        // When & Then
//        assertThrows(RuntimeException.class, () -> {
//            orderPaymentService.createSecondOrderPayment(customer, wallet, order, paymentAmount, admin);
//        });
//
//        verify(userValidator).requireAdminRole(admin);
//        verify(paymentFactory).createOrderSecondPayment(customer, wallet, order, paymentAmount, adminUuid);
//        verify(admin).getUserUuid();
//    }
//
//    // Tests for completeSecondOrderPayment
//    @Test
//    void completeSecondOrderPayment_ShouldCompleteSuccessfully() {
//        // When
//        OrderPayment result = orderPaymentService.completeSecondOrderPayment(orderPayment);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(orderPayment).complete();
//    }
//
//    @Test
//    void completeSecondOrderPayment_ShouldThrowException_WhenCompleteFails() {
//        // Given
//        doThrow(new IllegalStateException("Cannot complete second payment"))
//                .when(orderPayment).complete();
//
//        // When & Then
//        assertThrows(IllegalStateException.class, () -> {
//            orderPaymentService.completeSecondOrderPayment(orderPayment);
//        });
//
//        verify(orderPayment).complete();
//    }
//
//    // Tests for getOrderPaymentInfoByAdmin
//    @Test
//    void getOrderPaymentInfoByAdmin_ShouldReturnInfo_WhenUserIsAdmin() {
//        // Given
//        doNothing().when(userValidator).requireAdminRole(admin);
//
//        // When
//        OrderPayment result = orderPaymentService.getOrderPaymentInfoByAdmin(admin, orderPayment);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(userValidator).requireAdminRole(admin);
//    }
//
//    @Test
//    void getOrderPaymentInfoByAdmin_ShouldThrowException_WhenUserIsNotAdmin() {
//        // Given
//        doThrow(new SecurityException("Admin role required"))
//                .when(userValidator).requireAdminRole(admin);
//
//        // When & Then
//        assertThrows(SecurityException.class, () -> {
//            orderPaymentService.getOrderPaymentInfoByAdmin(admin, orderPayment);
//        });
//
//        verify(userValidator).requireAdminRole(admin);
//    }
//
//    // Tests for getOrderPaymentInfo
//    @Test
//    void getOrderPaymentInfo_ShouldReturnInfo() {
//        // When
//        OrderPayment result = orderPaymentService.getOrderPaymentInfo(orderPayment);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        // No interactions with validators should occur
//        verifyNoInteractions(userValidator);
//    }
//
//    // Tests for markAsFailed
//    @Test
//    void markAsFailed_ShouldMarkPaymentAsFailed() {
//        // Given
//        String reason = "Payment processing failed";
//
//        // When
//        OrderPayment result = orderPaymentService.markAsFailed(orderPayment, reason);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(orderPayment).markAsFailed(reason);
//    }
//
//    @Test
//    void markAsFailed_ShouldHandleNullReason() {
//        // When
//        OrderPayment result = orderPaymentService.markAsFailed(orderPayment, null);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(orderPayment).markAsFailed(null);
//    }
//
//    @Test
//    void markAsFailed_ShouldHandleEmptyReason() {
//        // Given
//        String emptyReason = "";
//
//        // When
//        OrderPayment result = orderPaymentService.markAsFailed(orderPayment, emptyReason);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(orderPayment).markAsFailed(emptyReason);
//    }
//
//    @Test
//    void markAsFailed_ShouldThrowException_WhenMarkAsFailedFails() {
//        // Given
//        String reason = "Payment failed";
//        doThrow(new IllegalStateException("Cannot mark as failed"))
//                .when(orderPayment).markAsFailed(reason);
//
//        // When & Then
//        assertThrows(IllegalStateException.class, () -> {
//            orderPaymentService.markAsFailed(orderPayment, reason);
//        });
//
//        verify(orderPayment).markAsFailed(reason);
//    }
//
//    // Integration tests for complete workflows
//    @Test
//    void firstPaymentWorkflow_ShouldExecuteSuccessfully() {
//        // Given
//        BigDecimal paymentAmount = new BigDecimal("1000.00");
//        UUID adminUuid = UUID.randomUUID();
//        when(admin.getUserUuid()).thenReturn(adminUuid);
//        doNothing().when(userValidator).requireAdminRole(admin);
//        when(paymentFactory.createOrderFirstPayment(customer, wallet, order, paymentAmount, adminUuid))
//                .thenReturn(orderPayment);
//
//        // When - Create first payment
//        OrderPayment createdPayment = orderPaymentService.crateOrderPayment(
//                customer, wallet, order, paymentAmount, admin
//        );
//
//        // When - Complete first payment
//        OrderPayment completedPayment = orderPaymentService.completePayment(createdPayment);
//
//        // When - Get payment info by admin
//        OrderPayment retrievedPayment = orderPaymentService.getOrderPaymentInfoByAdmin(admin, completedPayment);
//
//        // Then
//        assertNotNull(createdPayment);
//        assertNotNull(completedPayment);
//        assertNotNull(retrievedPayment);
//        assertEquals(orderPayment, createdPayment);
//        assertEquals(orderPayment, completedPayment);
//        assertEquals(orderPayment, retrievedPayment);
//
//        verify(userValidator, times(2)).requireAdminRole(admin); // Called twice
//        verify(paymentFactory).createOrderFirstPayment(customer, wallet, order, paymentAmount, adminUuid);
//        verify(orderPayment).complete();
//        verify(admin, times(1)).getUserUuid(); // Called once during creation
//    }
//
//    @Test
//    void secondPaymentWorkflow_ShouldExecuteSuccessfully() {
//        // Given
//        BigDecimal shippingAmount = new BigDecimal("25.00");
//        UUID adminUuid = UUID.randomUUID();
//        when(admin.getUserUuid()).thenReturn(adminUuid);
//        doNothing().when(userValidator).requireAdminRole(admin);
//        when(paymentFactory.createOrderSecondPayment(customer, wallet, order, shippingAmount, adminUuid))
//                .thenReturn(orderPayment);
//
//        // When - Create second payment
//        OrderPayment createdSecondPayment = orderPaymentService.createSecondOrderPayment(
//                customer, wallet, order, shippingAmount, admin
//        );
//
//        // When - Complete second payment
//        OrderPayment completedSecondPayment = orderPaymentService.completeSecondOrderPayment(createdSecondPayment);
//
//        // When - Get payment info
//        OrderPayment retrievedPayment = orderPaymentService.getOrderPaymentInfo(completedSecondPayment);
//
//        // Then
//        assertNotNull(createdSecondPayment);
//        assertNotNull(completedSecondPayment);
//        assertNotNull(retrievedPayment);
//        assertEquals(orderPayment, createdSecondPayment);
//        assertEquals(orderPayment, completedSecondPayment);
//        assertEquals(orderPayment, retrievedPayment);
//
//        verify(userValidator).requireAdminRole(admin); // Called once
//        verify(paymentFactory).createOrderSecondPayment(customer, wallet, order, shippingAmount, adminUuid);
//        verify(orderPayment).complete();
//        verify(admin).getUserUuid();
//    }
//
//    @Test
//    void failedPaymentWorkflow_ShouldExecuteSuccessfully() {
//        // Given
//        BigDecimal paymentAmount = new BigDecimal("1000.00");
//        String failureReason = "Insufficient funds";
//        UUID adminUuid = UUID.randomUUID();
//        when(admin.getUserUuid()).thenReturn(adminUuid);
//        doNothing().when(userValidator).requireAdminRole(admin);
//        when(paymentFactory.createOrderFirstPayment(customer, wallet, order, paymentAmount, adminUuid))
//                .thenReturn(orderPayment);
//
//        // When - Create payment
//        OrderPayment createdPayment = orderPaymentService.crateOrderPayment(
//                customer, wallet, order, paymentAmount, admin
//        );
//
//        // When - Mark as failed
//        OrderPayment failedPayment = orderPaymentService.markAsFailed(createdPayment, failureReason);
//
//        // When - Get payment info by admin
//        OrderPayment retrievedPayment = orderPaymentService.getOrderPaymentInfoByAdmin(admin, failedPayment);
//
//        // Then
//        assertNotNull(createdPayment);
//        assertNotNull(failedPayment);
//        assertNotNull(retrievedPayment);
//        assertEquals(orderPayment, createdPayment);
//        assertEquals(orderPayment, failedPayment);
//        assertEquals(orderPayment, retrievedPayment);
//
//        verify(userValidator, times(2)).requireAdminRole(admin); // Called twice
//        verify(paymentFactory).createOrderFirstPayment(customer, wallet, order, paymentAmount, adminUuid);
//        verify(orderPayment).markAsFailed(failureReason);
//        verify(admin).getUserUuid();
//    }
//
//    // Edge case tests
//    @Test
//    void crateOrderPayment_ShouldWork_WithLargeAmount() {
//        // Given
//        BigDecimal largeAmount = new BigDecimal("999999999.99");
//        UUID adminUuid = UUID.randomUUID();
//        when(admin.getUserUuid()).thenReturn(adminUuid);
//        doNothing().when(userValidator).requireAdminRole(admin);
//        when(paymentFactory.createOrderFirstPayment(customer, wallet, order, largeAmount, adminUuid))
//                .thenReturn(orderPayment);
//
//        // When
//        OrderPayment result = orderPaymentService.crateOrderPayment(
//                customer, wallet, order, largeAmount, admin
//        );
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(paymentFactory).createOrderFirstPayment(customer, wallet, order, largeAmount, adminUuid);
//    }
//
//    @Test
//    void createSecondOrderPayment_ShouldWork_WithMinimalAmount() {
//        // Given
//        BigDecimal minimalAmount = new BigDecimal("0.01");
//        UUID adminUuid = UUID.randomUUID();
//        when(admin.getUserUuid()).thenReturn(adminUuid);
//        doNothing().when(userValidator).requireAdminRole(admin);
//        when(paymentFactory.createOrderSecondPayment(customer, wallet, order, minimalAmount, adminUuid))
//                .thenReturn(orderPayment);
//
//        // When
//        OrderPayment result = orderPaymentService.createSecondOrderPayment(
//                customer, wallet, order, minimalAmount, admin
//        );
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(paymentFactory).createOrderSecondPayment(customer, wallet, order, minimalAmount, adminUuid);
//    }
//
//    @Test
//    void markAsFailed_ShouldWork_WithLongReason() {
//        // Given
//        String longReason = "This is a very detailed failure reason that explains exactly what went wrong during the payment processing, including technical details and potential solutions for the future.";
//
//        // When
//        OrderPayment result = orderPaymentService.markAsFailed(orderPayment, longReason);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderPayment, result);
//        verify(orderPayment).markAsFailed(longReason);
//    }
//}