package com.kijinkai.domain.payment.application.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;

import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;
import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
import com.kijinkai.domain.payment.application.port.out.WithdrawPersistenceRequestPort;
import com.kijinkai.domain.payment.domain.calculator.PaymentCalculator;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;

import com.kijinkai.domain.payment.domain.model.WithdrawRequest;

import com.kijinkai.domain.payment.domain.util.PaymentContents;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserRole;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.application.service.WalletApplicationService;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.apache.coyote.Request;
import org.assertj.core.api.Assertions;
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
class WithdrawRequestApplicationServiceTest {

    @Mock CustomerPersistencePort customerPersistencePort;
    @Mock UserPersistencePort userPersistencePort;
    @Mock WalletPersistencePort walletPersistencePort;
    @Mock WithdrawPersistenceRequestPort withdrawPersistenceRequestPort;

    @Mock ExchangeRateService exchangeRateService;
    @Mock PaymentCalculator paymentCalculator;
    @Mock WithdrawRequestApplicationService withdrawRequestService;
    @Mock PaymentFactory paymentFactory;
    @Mock PaymentMapper paymentMapper;

    @Mock WalletApplicationService walletApplicationService;

    @InjectMocks WithdrawRequestApplicationService withdrawRequestApplicationService;

    User user;
    Customer customer;
    Wallet wallet;

    ExchangeRate exchangeRate;
    ExchangeRateResponseDto exchangeRateResponseDto;


    WithdrawRequestDto withdrawRequestDto;
    WithdrawRequest withdrawRequest;
    WithdrawResponseDto withdrawResponseDto;
    BigDecimal covertAmount;
    BigDecimal totalAmount;

    @BeforeEach
    void setUp() {
        user = User.builder().userUuid(UUID.randomUUID()).build();
        customer = Customer.builder().userUuid(user.getUserUuid()).customerUuid(UUID.randomUUID()).build();
        wallet = Wallet.builder().walletUuid(UUID.randomUUID()).customerUuid(customer.getCustomerUuid()).build();

        exchangeRate = ExchangeRate.builder().rate(new BigDecimal(9.5)).build();
        exchangeRateResponseDto = ExchangeRateResponseDto.builder().rate(exchangeRate.getRate()).build();

        covertAmount = new BigDecimal("30000");
        totalAmount = new BigDecimal("100000");

        withdrawRequestDto = WithdrawRequestDto.builder()
                .requestAmount(new BigDecimal(30000))
                .currency(Currency.KRW)
                .bankName("kakao")
                .accountHolder("park jinhee")
                .accountNumber("1234-1234")
                .build();

        withdrawRequest = WithdrawRequest.builder()
                .requestUuid(UUID.randomUUID())
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .convertedAmount(covertAmount)
                .requestAmount(totalAmount)
                .status(WithdrawStatus.PENDING_ADMIN_APPROVAL)
                .build();

        withdrawResponseDto = WithdrawResponseDto.builder()
                .requestUuid(withdrawRequest.getRequestUuid())
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .status(withdrawRequest.getStatus())
                .requestAmount(withdrawRequest.getRequestAmount())
                .convertedAmount(withdrawRequest.getConvertedAmount())
                .build();
    }

    @Test
    void processWithdrawRequest() {
        //given
        BigDecimal withdrawFee = PaymentContents.WITHDRAWAL_FEE;

        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));
        when(exchangeRateService.getExchangeRateInfoByCurrency(withdrawRequestDto.getCurrency())).thenReturn(exchangeRateResponseDto);
        when(paymentCalculator.calculateWithdrawInJyp(withdrawRequestDto.getCurrency(), withdrawRequestDto.getRequestAmount())).thenReturn(covertAmount);
        when(paymentFactory.createWithdrawRequest(
                customer,wallet,withdrawRequestDto.getRequestAmount(),
                withdrawRequestDto.getCurrency(), withdrawFee, withdrawRequestDto.getBankName(), withdrawRequestDto.getAccountHolder(),  covertAmount, withdrawRequestDto.getAccountNumber(), exchangeRate.getRate()
        )).thenReturn(withdrawRequest);
        when(withdrawPersistenceRequestPort.saveWithdrawRequest(any(WithdrawRequest.class))).thenReturn(withdrawRequest);
        when(paymentMapper.createWithdrawResponse(withdrawRequest)).thenReturn(withdrawResponseDto);

        //when
        WithdrawResponseDto result = withdrawRequestApplicationService.processWithdrawRequest(user.getUserUuid(), withdrawRequestDto);

        //then
        assertThat(result).isNotNull();

    }

    @Test
    void approveWithdrawRequest() {
        //given
        User admin = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();
        WalletResponseDto walletResponseDto = WalletResponseDto.builder().walletUuid(UUID.randomUUID()).customerUuid(UUID.randomUUID()).build();

        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(withdrawPersistenceRequestPort.findByRequestUuid(withdrawRequest.getRequestUuid())).thenReturn(Optional.of(withdrawRequest));
        when(walletApplicationService.withdrawal(customer.getCustomerUuid(),wallet.getWalletUuid(),withdrawRequest.getTotalDeductAmount())).thenReturn(walletResponseDto);
        when(paymentMapper.approvedWithdrawResponse(withdrawRequest, walletResponseDto)).thenReturn(withdrawResponseDto);

        //when
        WithdrawResponseDto result = withdrawRequestApplicationService.approveWithdrawRequest(withdrawRequest.getRequestUuid(), admin.getUserUuid(), withdrawRequestDto);

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void getWithdrawInfoByAdmin() {
        //given
        User admin = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();

        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(withdrawPersistenceRequestPort.findByRequestUuid(withdrawRequest.getRequestUuid())).thenReturn(Optional.of(withdrawRequest));
        when(paymentMapper.withdrawInfoResponse(withdrawRequest)).thenReturn(withdrawResponseDto);

        //when
        WithdrawResponseDto result = withdrawRequestApplicationService.getWithdrawInfoByAdmin(withdrawRequest.getRequestUuid(), admin.getUserUuid());

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void getWithdraws() {
        //given

        PageRequest pageable = PageRequest.of(0, 10);
        List<WithdrawRequest> mockData = List.of(withdrawRequest);
        PageImpl<WithdrawRequest> mockPage = new PageImpl<>(mockData, pageable, mockData.size());


        when(userPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(user));
        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(withdrawPersistenceRequestPort.findAllByCustomerUuid(customer.getCustomerUuid(),pageable)).thenReturn(mockPage);
        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));

        when(paymentMapper.withdrawDetailsInfo(withdrawRequest,wallet.getBalance())).thenReturn(withdrawResponseDto);

        //when
        Page<WithdrawResponseDto> result = withdrawRequestApplicationService.getWithdraws(user.getUserUuid(), pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

    }

    @Test
    void getWithdrawByApprovalPending() {

        //given
        User admin = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();

        PageRequest pageable = PageRequest.of(0, 10);
        List<WithdrawRequest> mockData = List.of(withdrawRequest);
        PageImpl<WithdrawRequest> mockPage = new PageImpl<>(mockData, pageable, mockData.size());

        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(withdrawPersistenceRequestPort.findAllByWithdrawStatus(withdrawRequest.getBankName(), WithdrawStatus.PENDING_ADMIN_APPROVAL, pageable)).thenReturn(mockPage);

        //when
        Page<WithdrawResponseDto> result = withdrawRequestApplicationService.getWithdrawByApprovalPending(admin.getUserUuid(), withdrawRequest.getBankName(), pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getWithdrawInfo() {
        //given
        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(withdrawPersistenceRequestPort.findByRequestUuidAndCustomerUuid(withdrawRequest.getRequestUuid(),customer.getCustomerUuid())).thenReturn(Optional.of(withdrawRequest));
        when(paymentMapper.withdrawInfoResponse(withdrawRequest)).thenReturn(withdrawResponseDto);

        //when
        WithdrawResponseDto result = withdrawRequestApplicationService.getWithdrawInfo(withdrawRequest.getRequestUuid(), user.getUserUuid());

        //then
        assertThat(result).isNotNull();
    }
}