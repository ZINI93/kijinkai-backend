package com.kijinkai.domain.payment.domain.service;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.util.PaymentContents;
import com.kijinkai.domain.payment.domain.validator.PaymentValidator;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositRequestServiceTest {

    @Mock
    private PriceCalculationService priceCalculationService;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private PaymentFactory paymentFactory;

    @Mock
    private Customer customer;

    @Mock
    private Wallet wallet;

    @Mock
    private Currency originalCurrency;

    @Mock
    private DepositRequest depositRequest;

    @Mock
    private User user;

    private DepositRequestService depositRequestService;

    @BeforeEach
    void setUp() {
        depositRequestService = new DepositRequestService(
                priceCalculationService,
                userValidator,
                paymentValidator,
                paymentFactory
        );
    }

    // Tests for createDepositRequest
    @Test
    void createDepositRequest_ShouldCreateSuccessfully_WhenValidInput() {
        // Given
        BigDecimal originalAmount = new BigDecimal("1000.00");
        BigDecimal exchangeRate = new BigDecimal("150.00");
        String depositorName = "John Doe";
        String bankAccount = "1234567890";
        BigDecimal convertedAmount = new BigDecimal("1500.00");

        // PaymentContents.DEPOSIT_FEE는 실제 값을 사용하거나,
        // 서비스에서 주입받도록 리팩토링하는 것을 권장합니다.
        when(priceCalculationService.convertAndCalculateTotalInJpy(eq(originalAmount), eq(Currency.JPY), any(BigDecimal.class)))
                .thenReturn(convertedAmount);
        when(paymentFactory.createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, convertedAmount,
                exchangeRate, depositorName, bankAccount
        )).thenReturn(depositRequest);

        // When
        DepositRequest result = depositRequestService.createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, exchangeRate, depositorName, bankAccount
        );

        // Then
        assertNotNull(result);
        assertEquals(depositRequest, result);
        verify(paymentValidator).validateDepositEligibility(originalAmount, wallet);
        verify(priceCalculationService).convertAndCalculateTotalInJpy(eq(originalAmount), eq(Currency.JPY), any(BigDecimal.class));
        verify(paymentFactory).createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, convertedAmount,
                exchangeRate, depositorName, bankAccount
        );
    }

    @Test
    void createDepositRequest_ShouldThrowException_WhenValidationFails() {
        // Given
        BigDecimal originalAmount = new BigDecimal("1000.00");
        BigDecimal exchangeRate = new BigDecimal("150.00");
        String depositorName = "John Doe";
        String bankAccount = "1234567890";

        doThrow(new IllegalArgumentException("Invalid deposit amount"))
                .when(paymentValidator).validateDepositEligibility(originalAmount, wallet);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            depositRequestService.createDepositRequest(
                    customer, wallet, originalAmount, originalCurrency, exchangeRate, depositorName, bankAccount
            );
        });

        verify(paymentValidator).validateDepositEligibility(originalAmount, wallet);
        verifyNoInteractions(priceCalculationService);
        verifyNoInteractions(paymentFactory);
    }

    @Test
    void createDepositRequest_ShouldThrowException_WhenPriceCalculationFails() {
        // Given
        BigDecimal originalAmount = new BigDecimal("1000.00");
        BigDecimal exchangeRate = new BigDecimal("150.00");
        String depositorName = "John Doe";
        String bankAccount = "1234567890";

        when(priceCalculationService.convertAndCalculateTotalInJpy(eq(originalAmount), eq(Currency.JPY), any(BigDecimal.class)))
                .thenThrow(new RuntimeException("Price calculation failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            depositRequestService.createDepositRequest(
                    customer, wallet, originalAmount, originalCurrency, exchangeRate, depositorName, bankAccount
            );
        });

        verify(paymentValidator).validateDepositEligibility(originalAmount, wallet);
        verify(priceCalculationService).convertAndCalculateTotalInJpy(eq(originalAmount), eq(Currency.JPY), any(BigDecimal.class));
        verifyNoInteractions(paymentFactory);
    }

    @Test
    void createDepositRequest_ShouldHandleNullOptionalParameters() {
        // Given
        BigDecimal originalAmount = new BigDecimal("1000.00");
        BigDecimal exchangeRate = new BigDecimal("150.00");
        BigDecimal convertedAmount = new BigDecimal("1500.00");

        when(priceCalculationService.convertAndCalculateTotalInJpy(eq(originalAmount), eq(Currency.JPY), any(BigDecimal.class)))
                .thenReturn(convertedAmount);
        when(paymentFactory.createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, convertedAmount,
                exchangeRate, null, null
        )).thenReturn(depositRequest);

        // When
        DepositRequest result = depositRequestService.createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, exchangeRate, null, null
        );

        // Then
        assertNotNull(result);
        assertEquals(depositRequest, result);
        verify(paymentFactory).createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, convertedAmount,
                exchangeRate, null, null
        );
    }

    @Test
    void createDepositRequest_ShouldHandleZeroAmount() {
        // Given
        BigDecimal zeroAmount = BigDecimal.ZERO;
        BigDecimal exchangeRate = new BigDecimal("150.00");
        String depositorName = "John Doe";
        String bankAccount = "1234567890";
        BigDecimal convertedAmount = new BigDecimal("10.00"); // Only fee

        when(priceCalculationService.convertAndCalculateTotalInJpy(eq(zeroAmount), eq(Currency.JPY), any(BigDecimal.class)))
                .thenReturn(convertedAmount);
        when(paymentFactory.createDepositRequest(
                customer, wallet, zeroAmount, originalCurrency, convertedAmount,
                exchangeRate, depositorName, bankAccount
        )).thenReturn(depositRequest);

        // When
        DepositRequest result = depositRequestService.createDepositRequest(
                customer, wallet, zeroAmount, originalCurrency, exchangeRate, depositorName, bankAccount
        );

        // Then
        assertNotNull(result);
        assertEquals(depositRequest, result);
    }

    // Tests for approveDepositRequest
    @Test
    void approveDepositRequest_ShouldApproveSuccessfully() {
        // Given
        UUID adminUuid = UUID.randomUUID();
        String memo = "Approved by admin";

        // When
        DepositRequest result = depositRequestService.approveDepositRequest(depositRequest, adminUuid, memo);

        // Then
        assertNotNull(result);
        assertEquals(depositRequest, result);
        verify(depositRequest).approve(adminUuid, memo);
    }

    @Test
    void approveDepositRequest_ShouldHandleNullMemo() {
        // Given
        UUID adminUuid = UUID.randomUUID();

        // When
        DepositRequest result = depositRequestService.approveDepositRequest(depositRequest, adminUuid, null);

        // Then
        assertNotNull(result);
        assertEquals(depositRequest, result);
        verify(depositRequest).approve(adminUuid, null);
    }

    @Test
    void approveDepositRequest_ShouldThrowException_WhenApproveFails() {
        // Given
        UUID adminUuid = UUID.randomUUID();
        String memo = "Approval memo";
        doThrow(new IllegalStateException("Cannot approve request"))
                .when(depositRequest).approve(adminUuid, memo);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            depositRequestService.approveDepositRequest(depositRequest, adminUuid, memo);
        });

        verify(depositRequest).approve(adminUuid, memo);
    }

    // Tests for getDepositInfoByAdmin
    @Test
    void getDepositInfoByAdmin_ShouldReturnInfo_WhenUserIsAdmin() {
        // Given
        doNothing().when(userValidator).requireAdminRole(user);

        // When
        DepositRequest result = depositRequestService.getDepositInfoByAdmin(depositRequest, user);

        // Then
        assertNotNull(result);
        assertEquals(depositRequest, result);
        verify(userValidator).requireAdminRole(user);
    }

    @Test
    void getDepositInfoByAdmin_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        doThrow(new SecurityException("Admin role required"))
                .when(userValidator).requireAdminRole(user);

        // When & Then
        assertThrows(SecurityException.class, () -> {
            depositRequestService.getDepositInfoByAdmin(depositRequest, user);
        });

        verify(userValidator).requireAdminRole(user);
    }

    // Tests for getDepositInfo
    @Test
    void getDepositInfo_ShouldReturnInfo() {
        // When
        DepositRequest result = depositRequestService.getDepositInfo(depositRequest, customer);

        // Then
        assertNotNull(result);
        assertEquals(depositRequest, result);
        // No interactions with validators should occur
        verifyNoInteractions(userValidator);
    }

    // Tests for markAsFailed
    @Test
    void markAsFailed_ShouldMarkRequestAsFailed() {
        // Given
        String reason = "Bank transfer failed";

        // When
        depositRequestService.markAsFailed(depositRequest, reason);

        // Then
        verify(depositRequest).markAsFailed(reason);
    }

    @Test
    void markAsFailed_ShouldHandleNullReason() {
        // When
        depositRequestService.markAsFailed(depositRequest, null);

        // Then
        verify(depositRequest).markAsFailed(null);
    }

    @Test
    void markAsFailed_ShouldHandleEmptyReason() {
        // Given
        String emptyReason = "";

        // When
        depositRequestService.markAsFailed(depositRequest, emptyReason);

        // Then
        verify(depositRequest).markAsFailed(emptyReason);
    }

    @Test
    void markAsFailed_ShouldThrowException_WhenMarkAsFailedFails() {
        // Given
        String reason = "Failure reason";
        doThrow(new IllegalStateException("Cannot mark as failed"))
                .when(depositRequest).markAsFailed(reason);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            depositRequestService.markAsFailed(depositRequest, reason);
        });

        verify(depositRequest).markAsFailed(reason);
    }

    // Tests for expireOldRequests
    @Test
    void expireOldRequests_ShouldExpireExpiredRequests() {
        // Given
        DepositRequest expiredRequest1 = mock(DepositRequest.class);
        DepositRequest expiredRequest2 = mock(DepositRequest.class);
        DepositRequest validRequest = mock(DepositRequest.class);

        when(expiredRequest1.isExpired()).thenReturn(true);
        when(expiredRequest2.isExpired()).thenReturn(true);
        when(validRequest.isExpired()).thenReturn(false);

        List<DepositRequest> pendingRequests = Arrays.asList(expiredRequest1, validRequest, expiredRequest2);

        // When
        List<DepositRequest> result = depositRequestService.expireOldRequests(pendingRequests);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(expiredRequest1));
        assertTrue(result.contains(expiredRequest2));
        assertFalse(result.contains(validRequest));

        verify(expiredRequest1).isExpired();
        verify(expiredRequest1).expire();
        verify(expiredRequest2).isExpired();
        verify(expiredRequest2).expire();
        verify(validRequest).isExpired();
        verify(validRequest, never()).expire();
    }

    @Test
    void expireOldRequests_ShouldReturnEmptyList_WhenNoExpiredRequests() {
        // Given
        DepositRequest validRequest1 = mock(DepositRequest.class);
        DepositRequest validRequest2 = mock(DepositRequest.class);

        when(validRequest1.isExpired()).thenReturn(false);
        when(validRequest2.isExpired()).thenReturn(false);

        List<DepositRequest> pendingRequests = Arrays.asList(validRequest1, validRequest2);

        // When
        List<DepositRequest> result = depositRequestService.expireOldRequests(pendingRequests);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(validRequest1).isExpired();
        verify(validRequest1, never()).expire();
        verify(validRequest2).isExpired();
        verify(validRequest2, never()).expire();
    }

    @Test
    void expireOldRequests_ShouldReturnEmptyList_WhenInputIsEmpty() {
        // Given
        List<DepositRequest> emptyList = Collections.emptyList();

        // When
        List<DepositRequest> result = depositRequestService.expireOldRequests(emptyList);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void expireOldRequests_ShouldExpireAllRequests_WhenAllAreExpired() {
        // Given
        DepositRequest expiredRequest1 = mock(DepositRequest.class);
        DepositRequest expiredRequest2 = mock(DepositRequest.class);
        DepositRequest expiredRequest3 = mock(DepositRequest.class);

        when(expiredRequest1.isExpired()).thenReturn(true);
        when(expiredRequest2.isExpired()).thenReturn(true);
        when(expiredRequest3.isExpired()).thenReturn(true);

        List<DepositRequest> pendingRequests = Arrays.asList(expiredRequest1, expiredRequest2, expiredRequest3);

        // When
        List<DepositRequest> result = depositRequestService.expireOldRequests(pendingRequests);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(pendingRequests.size(), result.size());

        verify(expiredRequest1).expire();
        verify(expiredRequest2).expire();
        verify(expiredRequest3).expire();
    }

    // Integration tests for complete workflows
    @Test
    void completeDepositWorkflow_ShouldExecuteSuccessfully() {
        // Given
        BigDecimal originalAmount = new BigDecimal("1000.00");
        BigDecimal exchangeRate = new BigDecimal("150.00");
        String depositorName = "John Doe";
        String bankAccount = "1234567890";
        BigDecimal convertedAmount = new BigDecimal("1500.00");
        UUID adminUuid = UUID.randomUUID();
        String memo = "Deposit approved";

        when(priceCalculationService.convertAndCalculateTotalInJpy(eq(originalAmount), eq(Currency.JPY), any(BigDecimal.class)))
                .thenReturn(convertedAmount);
        when(paymentFactory.createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, convertedAmount,
                exchangeRate, depositorName, bankAccount
        )).thenReturn(depositRequest);
        doNothing().when(userValidator).requireAdminRole(user);

        // When - Create deposit request
        DepositRequest createdRequest = depositRequestService.createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, exchangeRate, depositorName, bankAccount
        );

        // When - Approve deposit request
        DepositRequest approvedRequest = depositRequestService.approveDepositRequest(
                createdRequest, adminUuid, memo
        );

        // When - Get deposit info by admin
        DepositRequest retrievedRequest = depositRequestService.getDepositInfoByAdmin(
                approvedRequest, user
        );

        // Then
        assertNotNull(createdRequest);
        assertNotNull(approvedRequest);
        assertNotNull(retrievedRequest);
        assertEquals(depositRequest, createdRequest);
        assertEquals(depositRequest, approvedRequest);
        assertEquals(depositRequest, retrievedRequest);

        verify(paymentValidator).validateDepositEligibility(originalAmount, wallet);
        verify(priceCalculationService).convertAndCalculateTotalInJpy(eq(originalAmount), eq(Currency.JPY), any(BigDecimal.class));
        verify(paymentFactory).createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, convertedAmount,
                exchangeRate, depositorName, bankAccount
        );
        verify(depositRequest).approve(adminUuid, memo);
        verify(userValidator).requireAdminRole(user);
    }

    @Test
    void failedDepositWorkflow_ShouldExecuteSuccessfully() {
        // Given
        BigDecimal originalAmount = new BigDecimal("1000.00");
        BigDecimal exchangeRate = new BigDecimal("150.00");
        String depositorName = "John Doe";
        String bankAccount = "1234567890";
        BigDecimal convertedAmount = new BigDecimal("1500.00");
        String failureReason = "Bank verification failed";

        when(priceCalculationService.convertAndCalculateTotalInJpy(eq(originalAmount), eq(Currency.JPY), any(BigDecimal.class)))
                .thenReturn(convertedAmount);
        when(paymentFactory.createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, convertedAmount,
                exchangeRate, depositorName, bankAccount
        )).thenReturn(depositRequest);

        // When - Create deposit request
        DepositRequest createdRequest = depositRequestService.createDepositRequest(
                customer, wallet, originalAmount, originalCurrency, exchangeRate, depositorName, bankAccount
        );

        // When - Mark as failed
        depositRequestService.markAsFailed(createdRequest, failureReason);

        // When - Get deposit info
        DepositRequest retrievedRequest = depositRequestService.getDepositInfo(createdRequest, customer);

        // Then
        assertNotNull(createdRequest);
        assertNotNull(retrievedRequest);
        assertEquals(depositRequest, createdRequest);
        assertEquals(depositRequest, retrievedRequest);

        verify(paymentValidator).validateDepositEligibility(originalAmount, wallet);
        verify(depositRequest).markAsFailed(failureReason);
    }

    // Edge case tests
    @Test
    void createDepositRequest_ShouldWork_WithLargeAmount() {
        // Given
        BigDecimal largeAmount = new BigDecimal("999999999.99");
        BigDecimal exchangeRate = new BigDecimal("150.00");
        String depositorName = "High Roller";
        String bankAccount = "9999999999";
        BigDecimal convertedAmount = new BigDecimal("150000000000.00");

        when(priceCalculationService.convertAndCalculateTotalInJpy(eq(largeAmount), eq(Currency.JPY), any(BigDecimal.class)))
                .thenReturn(convertedAmount);
        when(paymentFactory.createDepositRequest(
                customer, wallet, largeAmount, originalCurrency, convertedAmount,
                exchangeRate, depositorName, bankAccount
        )).thenReturn(depositRequest);

        // When
        DepositRequest result = depositRequestService.createDepositRequest(
                customer, wallet, largeAmount, originalCurrency, exchangeRate, depositorName, bankAccount
        );

        // Then
        assertNotNull(result);
        assertEquals(depositRequest, result);
    }

    @Test
    void markAsFailed_ShouldWork_WithLongReason() {
        // Given
        String longReason = "This deposit request failed due to multiple verification issues including invalid bank account information, insufficient depositor verification documents, and potential regulatory compliance concerns that require further investigation.";

        // When
        depositRequestService.markAsFailed(depositRequest, longReason);

        // Then
        verify(depositRequest).markAsFailed(longReason);
    }
}