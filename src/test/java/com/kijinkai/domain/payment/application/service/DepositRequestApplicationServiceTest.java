//package com.kijinkai.domain.payment.application.service;
//
//import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
//import com.kijinkai.domain.customer.domain.model.Customer;
//import com.kijinkai.domain.exchange.doamin.Currency;
//import com.kijinkai.domain.exchange.doamin.ExchangeRate;
//import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
//import com.kijinkai.domain.exchange.service.ExchangeRateService;
//import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
//import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
//import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
//import com.kijinkai.domain.payment.application.port.out.DepositRequestPersistencePort;
//import com.kijinkai.domain.payment.domain.calculator.PaymentCalculator;
//import com.kijinkai.domain.payment.domain.enums.BankType;
//import com.kijinkai.domain.payment.domain.enums.DepositStatus;
//import com.kijinkai.domain.payment.domain.factory.DepositRequestFactory;
//import com.kijinkai.domain.payment.domain.model.DepositRequest;
//import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
//import com.kijinkai.domain.user.domain.model.User;
//import com.kijinkai.domain.user.domain.model.UserRole;
//import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
//import com.kijinkai.domain.wallet.application.port.in.UpdateWalletUseCase;
//import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
//import com.kijinkai.domain.wallet.domain.model.Wallet;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
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
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class DepositRequestApplicationServiceTest {
//
//    @Mock DepositRequestPersistencePort depositRequestPersistencePort;
//    @Mock DepositRequestApplicationService depositRequestService;
//    @Mock CustomerPersistencePort customerPersistencePort;
//    @Mock WalletPersistencePort walletPersistencePort;
//    @Mock UserPersistencePort userPersistencePort;
//
//    @Mock PaymentCalculator paymentCalculator;
//    @Mock PaymentMapper paymentMapper;
//    @Mock DepositRequestFactory depositRequestFactory;
//
//    @Mock ExchangeRateService exchangeRateService;
//    @Mock UpdateWalletUseCase updateWalletUseCase;
//
//    @InjectMocks DepositRequestApplicationService depositRequestApplicationService;
//
//    User user;
//    Customer customer;
//    Wallet wallet;
//
//    DepositRequest depositRequest;
//    DepositRequestDto depositRequestDto;
//    DepositRequestResponseDto depositRequestResponseDto;
//
//
//    ExchangeRateResponseDto exchangeRateResponseDto;
//    ExchangeRate exchangeRate;
//    BigDecimal convertedAmount;
//
//    @BeforeEach
//    void setUp() {
//
//        user = User.builder().userUuid(UUID.randomUUID()).build();
//        customer = Customer.builder().customerUuid(UUID.randomUUID()).build();
//        wallet = Wallet.builder().customerUuid(customer.getCustomerUuid()).walletUuid(UUID.randomUUID()).build();
//
//
//        depositRequestDto = DepositRequestDto.builder()
//                .depositorName("jinhee park")
//                .amountOriginal(new BigDecimal(10000))
//                .bankType(BankType.KAKAO)
//                .originalCurrency(Currency.KRW)
//                .build();
//
//
//        exchangeRate = ExchangeRate
//                .builder()
//                .currency(Currency.JPY)
//                .rate(new BigDecimal(9))
//                .build();
//
//
//        convertedAmount = paymentCalculator.calculateDepositInJpy(Currency.JPY, depositRequestDto.getAmountOriginal());
//
//        exchangeRateResponseDto = ExchangeRateResponseDto.builder()
//                .currency(exchangeRate.getCurrency())
//                .rate(exchangeRate.getRate())
//                .build();
//
//
//        depositRequest = DepositRequest.builder()
//                .requestUuid(UUID.randomUUID())
//                .customerUuid(customer.getCustomerUuid())
//                .walletUuid(wallet.getWalletUuid())
//                .amountOriginal(depositRequestDto.getAmountOriginal())
//                .currencyOriginal(depositRequestDto.getOriginalCurrency())
//                .exchangeRate(exchangeRate.getRate())
//                .bankType(depositRequestDto.getBankType())
//                .amountConverted(convertedAmount)
//                .status(DepositStatus.PENDING_ADMIN_APPROVAL)
//                .expiresAt(LocalDateTime.now().plusDays(2))
//                .depositorName(depositRequestDto.getDepositorName())
//                .build();
//
//        depositRequestResponseDto = DepositRequestResponseDto
//                .builder()
//                .requestUuid(depositRequest.getRequestUuid())
//                .currencyOriginal(depositRequest.getCurrencyOriginal())
//                .amountOriginal(depositRequest.getAmountOriginal())
//                .build();
//
//
//    }
//
//    @Test
//    void processDepositRequest() {
//        //given
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
//        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));
//        when(exchangeRateService.getExchangeRateInfoByCurrency(depositRequestDto.getOriginalCurrency())).thenReturn(exchangeRateResponseDto);
//        when(paymentCalculator.calculateDepositInJpy(exchangeRate.getCurrency(),depositRequest.getAmountOriginal())).thenReturn(convertedAmount);
//        when(depositRequestFactory.createDepositRequest(
//                customer, wallet, depositRequest.getAmountOriginal(), depositRequest.getCurrencyOriginal(),
//                convertedAmount , exchangeRate.getRate(), depositRequest.getDepositorName(), depositRequest.getBankType()  ))
//                .thenReturn(depositRequest);
//        when(depositRequestPersistencePort.saveDepositRequest(any(DepositRequest.class))).thenReturn(depositRequest);
//        when(paymentMapper.createDepositResponse(depositRequest)).thenReturn(depositRequestResponseDto);
//
//        //when
//        DepositRequestResponseDto result = depositRequestApplicationService.processDepositRequest(user.getUserUuid(), depositRequestDto);
//
//        //then
//        assertThat(result.getRequestUuid()).isEqualTo(depositRequest.getRequestUuid());
//        assertThat(result.getAmountOriginal()).isEqualTo(depositRequest.getAmountOriginal());
//
//        verify(depositRequestPersistencePort,times(1)).saveDepositRequest(depositRequest);
//    }
//
//    @Test
//    @DisplayName("유저 - 유저 입금내역 조회")
//    void getDepositRequestInfo() {
//        //given
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
//        when(depositRequestPersistencePort.findByCustomerUuidAndRequestUuid(customer.getCustomerUuid(),depositRequest.getRequestUuid())).thenReturn(Optional.of(depositRequest));
//        when(paymentMapper.depositInfoResponse(depositRequest)).thenReturn(depositRequestResponseDto);
//
//        //when
//        DepositRequestResponseDto result = depositRequestApplicationService.getDepositRequestInfo(depositRequest.getRequestUuid(), user.getUserUuid());
//
//        //then
//        assertThat(result.getRequestUuid()).isEqualTo(depositRequest.getRequestUuid());
//        assertThat(result.getAmountOriginal()).isEqualTo(depositRequest.getAmountOriginal());
//
//    }
//
//    @Test
//    @DisplayName("관리자 - 유저 입금내역 조회")
//    void getDepositRequestInfoByAdmin() {
//        //given
//        User admin  = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();
//
//        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
//        when(depositRequestPersistencePort.findByRequestUuid(depositRequest.getRequestUuid())).thenReturn(Optional.of(depositRequest));
//        when(paymentMapper.depositInfoResponse(depositRequest)).thenReturn(depositRequestResponseDto);
//
//        //when
//        DepositRequestResponseDto result = depositRequestApplicationService.getDepositRequestInfoByAdmin(depositRequest.getRequestUuid(), admin.getUserUuid());
//
//        //then
//        assertThat(result.getRequestUuid()).isEqualTo(depositRequest.getRequestUuid());
//        assertThat(result.getAmountConverted()).isEqualTo(depositRequest.getAmountConverted());
//
//    }
//
////    @Test
////    void getDepositsByApprovalPending() {
////        //given
////
////        PageRequest pageable = PageRequest.of(0, 10);
////        List<DepositRequest> mockData = List.of(depositRequest);
////        PageImpl<DepositRequest> mockPage = new PageImpl<>(mockData, pageable, mockData.size());
////
////        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
////        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));
////        when(depositRequestPersistencePort.findByDepositPaymentUuidByStatus(customer.getCustomerUuid(), depositRequestDto.getDepositorName(), DepositStatus.PENDING_ADMIN_APPROVAL, pageable)).thenReturn(mockPage);
////
////        //when
////        Page<DepositRequestResponseDto> result = depositRequestApplicationService.getDepositsByApprovalPendingByAdmin(user.getUserUuid(), depositRequest.getDepositorName(), pageable);
////
////        //then
////        assertThat(result.getTotalElements()).isEqualTo(1);
////        assertThat(result.getContent()).hasSize(1);
////    }
//
//    @Test
//    void getDeposits() {
//        //given
//        PageRequest pageable = PageRequest.of(0, 10);
//        List<DepositRequest> mockData = List.of(depositRequest);
//        PageImpl<DepositRequest> mockPage = new PageImpl<>(mockData, pageable, mockData.size());
//
//        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
//        when(depositRequestPersistencePort.findAllByCustomerUuid(customer.getCustomerUuid(), pageable)).thenReturn(mockPage);
//
//        //when
//        Page<DepositRequestResponseDto> result = depositRequestApplicationService.getDeposits(user.getUserUuid(), pageable);
//
//        //then
//        assertThat(result.getTotalElements()).isEqualTo(1);
//        assertThat(result.getContent()).hasSize(1);
//    }
//
//    @Test
//    void expireOldRequests() {
//        //given
//        DepositRequest depositRequestByExpired = DepositRequest.builder()
//                .status(DepositStatus.EXPIRED)
//                .build();
//
//        List<DepositRequest> emptyList = List.of();
//
//        when(depositRequestPersistencePort.findByStatus(DepositStatus.PENDING_ADMIN_APPROVAL)).thenReturn(List.of(depositRequest));
//        when(depositRequestPersistencePort.saveAllDeposit(emptyList)).thenReturn(emptyList);
//
//        //when
//        List<DepositRequestResponseDto> result = depositRequestApplicationService.expireOldRequests();
//
//        //then
//        assertThat(result.size()).isEqualTo(0);
//
//        verify(depositRequestPersistencePort,times(1)).saveAllDeposit(emptyList);
//    }
//
//
//    @Test
//    @DisplayName("관리자 - 입금 승인" )
//    void approveDepositRequest() {
//        //given
//        WalletResponseDto walletResponseDto = WalletResponseDto.builder().build();
//        User admin  = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();
//
//        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
//        when(depositRequestPersistencePort.findByRequestUuid(depositRequest.getRequestUuid())).thenReturn(Optional.of(depositRequest));
//        when(updateWalletUseCase.deposit(depositRequest.getCustomerUuid(),depositRequest.getWalletUuid(),depositRequest.getAmountConverted())).thenReturn(walletResponseDto);
//        when(depositRequestPersistencePort.saveDepositRequest(any(DepositRequest.class))).thenReturn(depositRequest);
//        when(paymentMapper.approveDepositResponse(depositRequest, walletResponseDto)).thenReturn(depositRequestResponseDto);
//
//        //when
//        DepositRequestResponseDto result = depositRequestApplicationService.approveDepositRequest(depositRequest.getRequestUuid(), admin.getUserUuid(), depositRequestDto);
//
//        //then
//        assertThat(result.getRequestUuid()).isEqualTo(depositRequest.getRequestUuid());
//        assertThat(result.getAmountConverted()).isEqualTo(depositRequest.getAmountConverted());
//
//        verify(depositRequestPersistencePort,times(1)).saveDepositRequest(depositRequest);
//    }
//}