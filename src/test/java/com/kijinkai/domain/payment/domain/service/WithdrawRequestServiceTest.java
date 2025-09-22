package com.kijinkai.domain.payment.domain.service;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.validator.PaymentValidator;
import com.kijinkai.domain.user.adapter.in.web.validator.UserValidator;
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
class WithdrawRequestServiceTest {

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PaymentFactory paymentFactory;

    @Mock
    private Customer customer;

    @Mock
    private Wallet wallet;

    @Mock
    private WithdrawRequest withdrawRequest;

    @Mock
    private User user;

    @Mock
    private Currency targetCurrency;

    private WithdrawRequestService withdrawRequestService;

    @BeforeEach
    void setUp() {
        withdrawRequestService = new WithdrawRequestService(
                paymentValidator,
                userValidator,
                paymentFactory
        );
    }

    @Test
    void createWithdrawRequest_ShouldCreateSuccessfully_WhenValidInput() {
        // Given
        BigDecimal requestAmount = new BigDecimal("1000.00");
        String bankName = "Test Bank";
        String accountHolder = "John Doe";
        BigDecimal withdrawFee = new BigDecimal("10.00");
        BigDecimal convertedAmount = new BigDecimal("990.00");

        when(paymentFactory.createWithdrawRequest(
                customer, wallet, requestAmount, targetCurrency,
                withdrawFee, bankName, accountHolder, convertedAmount
        )).thenReturn(withdrawRequest);

        // When
        WithdrawRequest result = withdrawRequestService.createWithdrawRequest(
                customer, wallet, requestAmount, targetCurrency,
                bankName, accountHolder, withdrawFee, convertedAmount
        );

        // Then
        assertNotNull(result);
        assertEquals(withdrawRequest, result);
        verify(paymentValidator).validateWithdrawEligibility(requestAmount);
        verify(paymentFactory).createWithdrawRequest(
                customer, wallet, requestAmount, targetCurrency,
                withdrawFee, bankName, accountHolder, convertedAmount
        );
    }

    @Test
    void createWithdrawRequest_ShouldThrowException_WhenValidationFails() {
        // Given
        BigDecimal requestAmount = new BigDecimal("1000.00");
        String bankName = "Test Bank";
        String accountHolder = "John Doe";
        BigDecimal withdrawFee = new BigDecimal("10.00");
        BigDecimal convertedAmount = new BigDecimal("990.00");

        doThrow(new IllegalArgumentException("Invalid amount"))
                .when(paymentValidator).validateWithdrawEligibility(requestAmount);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            withdrawRequestService.createWithdrawRequest(
                    customer, wallet, requestAmount, targetCurrency,
                    bankName, accountHolder, withdrawFee, convertedAmount
            );
        });

        verify(paymentValidator).validateWithdrawEligibility(requestAmount);
        verify(paymentFactory, never()).createWithdrawRequest(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void approveWithdrawRequest_ShouldApproveSuccessfully() {
        // Given
        UUID adminUuid = UUID.randomUUID();
        String memo = "Approved by admin";
        BigDecimal exchangeRate = new BigDecimal("1.2");

        // When
        WithdrawRequest result = withdrawRequestService.approveWithdrawRequest(
                withdrawRequest, adminUuid, memo, exchangeRate
        );

        // Then
        assertNotNull(result);
        assertEquals(withdrawRequest, result);
        verify(withdrawRequest).approve(adminUuid, memo, exchangeRate);
    }

    @Test
    void getWithdrawInfoByAdmin_ShouldReturnInfo_WhenUserIsAdmin() {
        // Given
        doNothing().when(userValidator).requireAdminRole(user);

        // When
        WithdrawRequest result = withdrawRequestService.getWithdrawInfoByAdmin(withdrawRequest, user);

        // Then
        assertNotNull(result);
        assertEquals(withdrawRequest, result);
        verify(userValidator).requireAdminRole(user);
    }

    @Test
    void getWithdrawInfoByAdmin_ShouldThrowException_WhenUserIsNotAdmin() {
        // Given
        doThrow(new SecurityException("Admin role required"))
                .when(userValidator).requireAdminRole(user);

        // When & Then
        assertThrows(SecurityException.class, () -> {
            withdrawRequestService.getWithdrawInfoByAdmin(withdrawRequest, user);
        });

        verify(userValidator).requireAdminRole(user);
    }

    @Test
    void getWithdrawInfo_ShouldReturnInfo() {
        // When
        WithdrawRequest result = withdrawRequestService.getWithdrawInfo(withdrawRequest);

        // Then
        assertNotNull(result);
        assertEquals(withdrawRequest, result);
    }

    @Test
    void markAsFailed_ShouldMarkRequestAsFailed() {
        // Given
        String reason = "Payment processing failed";

        // When
        withdrawRequestService.markAsFailed(withdrawRequest, reason);

        // Then
        verify(withdrawRequest).markAsFailed(reason);
    }

    @Test
    void createWithdrawRequest_ShouldHandleNullValues() {
        // Given
        BigDecimal requestAmount = new BigDecimal("1000.00");
        BigDecimal withdrawFee = new BigDecimal("10.00");
        BigDecimal convertedAmount = new BigDecimal("990.00");

        when(paymentFactory.createWithdrawRequest(
                customer, wallet, requestAmount, targetCurrency,
                withdrawFee, null, null, convertedAmount
        )).thenReturn(withdrawRequest);

        // When
        WithdrawRequest result = withdrawRequestService.createWithdrawRequest(
                customer, wallet, requestAmount, targetCurrency,
                null, null, withdrawFee, convertedAmount
        );

        // Then
        assertNotNull(result);
        verify(paymentValidator).validateWithdrawEligibility(requestAmount);
        verify(paymentFactory).createWithdrawRequest(
                customer, wallet, requestAmount, targetCurrency,
                withdrawFee, null, null, convertedAmount
        );
    }

    @Test
    void approveWithdrawRequest_ShouldHandleNullMemo() {
        // Given
        UUID adminUuid = UUID.randomUUID();
        BigDecimal exchangeRate = new BigDecimal("1.2");

        // When
        WithdrawRequest result = withdrawRequestService.approveWithdrawRequest(
                withdrawRequest, adminUuid, null, exchangeRate
        );

        // Then
        assertNotNull(result);
        verify(withdrawRequest).approve(adminUuid, null, exchangeRate);
    }

    @Test
    void markAsFailed_ShouldHandleEmptyReason() {
        // Given
        String emptyReason = "";

        // When
        withdrawRequestService.markAsFailed(withdrawRequest, emptyReason);

        // Then
        verify(withdrawRequest).markAsFailed(emptyReason);
    }

    // Integration-style test for complete workflow
    @Test
    void completeWithdrawWorkflow_ShouldExecuteSuccessfully() {
        // Given
        BigDecimal requestAmount = new BigDecimal("1000.00");
        String bankName = "Test Bank";
        String accountHolder = "John Doe";
        BigDecimal withdrawFee = new BigDecimal("10.00");
        BigDecimal convertedAmount = new BigDecimal("990.00");
        UUID adminUuid = UUID.randomUUID();
        String memo = "Approved";
        BigDecimal exchangeRate = new BigDecimal("1.2");

        when(paymentFactory.createWithdrawRequest(
                customer, wallet, requestAmount, targetCurrency,
                withdrawFee, bankName, accountHolder, convertedAmount
        )).thenReturn(withdrawRequest);

        // When - Create request
        WithdrawRequest createdRequest = withdrawRequestService.createWithdrawRequest(
                customer, wallet, requestAmount, targetCurrency,
                bankName, accountHolder, withdrawFee, convertedAmount
        );

        // When - Approve request
        WithdrawRequest approvedRequest = withdrawRequestService.approveWithdrawRequest(
                createdRequest, adminUuid, memo, exchangeRate
        );

        // Then
        assertNotNull(createdRequest);
        assertNotNull(approvedRequest);
        assertEquals(withdrawRequest, createdRequest);
        assertEquals(withdrawRequest, approvedRequest);

        verify(paymentValidator).validateWithdrawEligibility(requestAmount);
        verify(paymentFactory).createWithdrawRequest(
                customer, wallet, requestAmount, targetCurrency,
                withdrawFee, bankName, accountHolder, convertedAmount
        );
        verify(withdrawRequest).approve(adminUuid, memo, exchangeRate);
    }
}