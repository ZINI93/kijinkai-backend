package com.kijinkai.domain.payment.domain.service;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.payment.domain.entity.RefundRequest;
import com.kijinkai.domain.payment.domain.enums.RefundType;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundRequestServiceTest {

    @Mock
    private PaymentFactory paymentFactory;

    @Mock
    private UserValidator userValidator;

    @Mock
    private Customer customer;

    @Mock
    private Wallet wallet;

    @Mock
    private OrderItem orderItem;

    @Mock
    private RefundRequest refundRequest;

    @Mock
    private User admin;

    @Mock
    private RefundType refundType;

    private RefundRequestService refundRequestService;

    @BeforeEach
    void setUp() {
        refundRequestService = new RefundRequestService(paymentFactory, userValidator);
    }

    @Test
    void createRefundRequest_ShouldCreateSuccessfully_WhenValidInput() {
        // Given
        BigDecimal refundAmount = new BigDecimal("500.00");
        UUID adminUuid = UUID.randomUUID();
        String refundReason = "Product defect";

        when(paymentFactory.createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        )).thenReturn(refundRequest);

        // When
        RefundRequest result = refundRequestService.createRefundRequest(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        );

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        verify(paymentFactory).createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        );
    }

    @Test
    void createRefundRequest_ShouldHandleNullValues() {
        // Given
        BigDecimal refundAmount = new BigDecimal("500.00");
        UUID adminUuid = UUID.randomUUID();

        when(paymentFactory.createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, null, refundType
        )).thenReturn(refundRequest);

        // When
        RefundRequest result = refundRequestService.createRefundRequest(
                customer, wallet, orderItem, refundAmount, adminUuid, null, refundType
        );

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        verify(paymentFactory).createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, null, refundType
        );
    }

    @Test
    void createRefundRequest_ShouldHandleZeroRefundAmount() {
        // Given
        BigDecimal refundAmount = BigDecimal.ZERO;
        UUID adminUuid = UUID.randomUUID();
        String refundReason = "Promotional refund";

        when(paymentFactory.createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        )).thenReturn(refundRequest);

        // When
        RefundRequest result = refundRequestService.createRefundRequest(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        );

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        verify(paymentFactory).createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        );
    }

    @Test
    void createRefundRequest_ShouldThrowException_WhenFactoryFails() {
        // Given
        BigDecimal refundAmount = new BigDecimal("500.00");
        UUID adminUuid = UUID.randomUUID();
        String refundReason = "Product defect";

        when(paymentFactory.createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        )).thenThrow(new RuntimeException("Factory error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            refundRequestService.createRefundRequest(
                    customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
            );
        });

        verify(paymentFactory).createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        );
    }

    @Test
    void processRefundRequest_ShouldProcessSuccessfully_WhenAdminHasValidRole() {
        // Given
        String memo = "Refund processed successfully";
        doNothing().when(userValidator).requireAdminRole(admin);

        // When
        RefundRequest result = refundRequestService.processRefundRequest(refundRequest, admin, memo);

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        verify(userValidator).requireAdminRole(admin);
        verify(refundRequest).complete(memo);
    }

    @Test
    void processRefundRequest_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        String memo = "Refund processed";
        doThrow(new SecurityException("Admin role required"))
                .when(userValidator).requireAdminRole(admin);

        // When & Then
        assertThrows(SecurityException.class, () -> {
            refundRequestService.processRefundRequest(refundRequest, admin, memo);
        });

        verify(userValidator).requireAdminRole(admin);
        verify(refundRequest, never()).complete(anyString());
    }

    @Test
    void processRefundRequest_ShouldHandleNullMemo() {
        // Given
        doNothing().when(userValidator).requireAdminRole(admin);

        // When
        RefundRequest result = refundRequestService.processRefundRequest(refundRequest, admin, null);

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        verify(userValidator).requireAdminRole(admin);
        verify(refundRequest).complete(null);
    }

    @Test
    void processRefundRequest_ShouldHandleEmptyMemo() {
        // Given
        String emptyMemo = "";
        doNothing().when(userValidator).requireAdminRole(admin);

        // When
        RefundRequest result = refundRequestService.processRefundRequest(refundRequest, admin, emptyMemo);

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        verify(userValidator).requireAdminRole(admin);
        verify(refundRequest).complete(emptyMemo);
    }

    @Test
    void processRefundRequest_ShouldThrowException_WhenCompleteMethodFails() {
        // Given
        String memo = "Processing memo";
        doNothing().when(userValidator).requireAdminRole(admin);
        doThrow(new IllegalStateException("Cannot complete refund"))
                .when(refundRequest).complete(memo);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            refundRequestService.processRefundRequest(refundRequest, admin, memo);
        });

        verify(userValidator).requireAdminRole(admin);
        verify(refundRequest).complete(memo);
    }

    @Test
    void getRefundInfoByAdmin_ShouldReturnInfo_WhenUserIsAdmin() {
        // Given
        doNothing().when(userValidator).requireAdminRole(admin);

        // When
        RefundRequest result = refundRequestService.getRefundInfoByAdmin(refundRequest, admin);

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        verify(userValidator).requireAdminRole(admin);
    }

    @Test
    void getRefundInfoByAdmin_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        doThrow(new SecurityException("Admin role required"))
                .when(userValidator).requireAdminRole(admin);

        // When & Then
        assertThrows(SecurityException.class, () -> {
            refundRequestService.getRefundInfoByAdmin(refundRequest, admin);
        });

        verify(userValidator).requireAdminRole(admin);
    }

    @Test
    void getRefundInfo_ShouldReturnInfo() {
        // When
        RefundRequest result = refundRequestService.getRefundInfo(refundRequest);

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        // No interactions with validators should occur
        verifyNoInteractions(userValidator);
    }

    // Integration-style test for complete refund workflow
    @Test
    void completeRefundWorkflow_ShouldExecuteSuccessfully() {
        // Given
        BigDecimal refundAmount = new BigDecimal("500.00");
        UUID adminUuid = UUID.randomUUID();
        String refundReason = "Customer requested";
        String processingMemo = "Refund approved and processed";

        when(paymentFactory.createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        )).thenReturn(refundRequest);

        doNothing().when(userValidator).requireAdminRole(admin);

        // When - Create refund request
        RefundRequest createdRequest = refundRequestService.createRefundRequest(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        );

        // When - Process refund request
        RefundRequest processedRequest = refundRequestService.processRefundRequest(
                createdRequest, admin, processingMemo
        );

        // When - Get refund info by admin
        RefundRequest retrievedRequest = refundRequestService.getRefundInfoByAdmin(
                processedRequest, admin
        );

        // Then
        assertNotNull(createdRequest);
        assertNotNull(processedRequest);
        assertNotNull(retrievedRequest);
        assertEquals(refundRequest, createdRequest);
        assertEquals(refundRequest, processedRequest);
        assertEquals(refundRequest, retrievedRequest);

        verify(paymentFactory).createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, refundReason, refundType
        );
        verify(userValidator, times(2)).requireAdminRole(admin); // Called twice
        verify(refundRequest).complete(processingMemo);
    }

    // Edge case tests
    @Test
    void createRefundRequest_ShouldWork_WithLargeRefundAmount() {
        // Given
        BigDecimal largeRefundAmount = new BigDecimal("999999999.99");
        UUID adminUuid = UUID.randomUUID();
        String refundReason = "Large amount refund";

        when(paymentFactory.createRefundPayment(
                customer, wallet, orderItem, largeRefundAmount, adminUuid, refundReason, refundType
        )).thenReturn(refundRequest);

        // When
        RefundRequest result = refundRequestService.createRefundRequest(
                customer, wallet, orderItem, largeRefundAmount, adminUuid, refundReason, refundType
        );

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        verify(paymentFactory).createRefundPayment(
                customer, wallet, orderItem, largeRefundAmount, adminUuid, refundReason, refundType
        );
    }

    @Test
    void createRefundRequest_ShouldWork_WithLongRefundReason() {
        // Given
        BigDecimal refundAmount = new BigDecimal("100.00");
        UUID adminUuid = UUID.randomUUID();
        String longRefundReason = "This is a very long refund reason that describes in detail why the customer is requesting a refund for their purchase and all the circumstances surrounding this request.";

        when(paymentFactory.createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, longRefundReason, refundType
        )).thenReturn(refundRequest);

        // When
        RefundRequest result = refundRequestService.createRefundRequest(
                customer, wallet, orderItem, refundAmount, adminUuid, longRefundReason, refundType
        );

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        verify(paymentFactory).createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid, longRefundReason, refundType
        );
    }

    @Test
    void processRefundRequest_ShouldWork_WithLongMemo() {
        // Given
        String longMemo = "This is a very detailed processing memo that contains extensive information about how the refund was processed, including timestamps, verification steps, and additional notes.";
        doNothing().when(userValidator).requireAdminRole(admin);

        // When
        RefundRequest result = refundRequestService.processRefundRequest(refundRequest, admin, longMemo);

        // Then
        assertNotNull(result);
        assertEquals(refundRequest, result);
        verify(userValidator).requireAdminRole(admin);
        verify(refundRequest).complete(longMemo);
    }
}