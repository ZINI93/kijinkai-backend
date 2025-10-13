package com.kijinkai.domain.wallet.application.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserRole;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletStatus;
import com.kijinkai.domain.wallet.application.dto.WalletBalanceResponseDto;
import com.kijinkai.domain.wallet.application.dto.WalletFreezeRequest;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.application.mapper.WalletMapper;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.domain.fectory.WalletFactory;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletApplicationServiceTest {

    @Mock
    WalletPersistencePort walletPersistencePort;
    @Mock
    WalletMapper walletMapper;
    @Mock
    WalletFactory walletFactory;

    //outer
    @Mock
    UserPersistencePort userPersistencePort;
    @Mock
    CustomerPersistencePort customerPersistencePort;

    @InjectMocks
    WalletApplicationService walletApplicationService;

    User user;
    Customer customer;
    Wallet wallet;
    WalletResponseDto walletResponseDto;
    WalletBalanceResponseDto walletBalanceResponseDto;
    WalletFreezeRequest walletFreezeRequest;

    @BeforeEach
    void setUp() {

        user = User.builder().userUuid(UUID.randomUUID()).build();
        customer = Customer.builder().userUuid(user.getUserUuid()).customerUuid(UUID.randomUUID()).build();

        wallet = Wallet.builder()
                .walletUuid(UUID.randomUUID())
                .customerUuid(customer.getCustomerUuid())
                .balance(new BigDecimal(10000.00))
                .walletStatus(WalletStatus.ACTIVE)
                .freezeReason("없음")
                .build();

        walletResponseDto = WalletResponseDto.builder()
                .walletUuid(wallet.getWalletUuid())
                .customerUuid(wallet.getCustomerUuid())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .walletStatus(wallet.getWalletStatus())
                .build();

        walletBalanceResponseDto = WalletBalanceResponseDto
                .builder()
                .balance(wallet.getBalance())
                .build();

    }

    @Test
    void createWallet() {
        //given

        when(walletFactory.createWallet(customer.getCustomerUuid())).thenReturn(wallet);
        when(walletPersistencePort.saveWallet(any(Wallet.class))).thenReturn(wallet);
        when(walletMapper.toResponse(wallet)).thenReturn(walletResponseDto);
        //when
        WalletResponseDto result = walletApplicationService.createWallet(customer);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getWalletStatus()).isEqualTo(wallet.getWalletStatus());

        verify(walletPersistencePort, times(1)).saveWallet(wallet);
    }

    @Test
    @DisplayName("고객 지갑 잔액조회")
    void getWalletBalance() {
        //given
        when(customerPersistencePort.findByUserUuid(user.getUserUuid())).thenReturn(Optional.of(customer));
        when(walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())).thenReturn(Optional.of(wallet));
        when(walletMapper.balanceMapper(wallet.getBalance())).thenReturn(walletBalanceResponseDto);
        //when
        WalletBalanceResponseDto result = walletApplicationService.getWalletBalance(user.getUserUuid());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(wallet.getBalance());

        verify(customerPersistencePort, times(1)).findByUserUuid(user.getUserUuid());
        verify(walletPersistencePort, times(1)).findByCustomerUuid(customer.getCustomerUuid());
    }

    @Test
    void getCustomerWalletBalanceByAdmin() {
        //given
        //when
        //then
    }

    @Test
    void deposit() {
        //given

        BigDecimal depositAmount = new BigDecimal(10000.00);


        WalletResponseDto walletDepositResponseDto = WalletResponseDto
                .builder()
                .walletUuid(wallet.getWalletUuid())
                .customerUuid(customer.getCustomerUuid())
                .balance(wallet.getBalance().add(depositAmount))
                .build();

        when(walletPersistencePort.findByCustomerUuidAndWalletUuid(customer.getCustomerUuid(), wallet.getWalletUuid())).thenReturn(Optional.of(wallet));
        when(walletPersistencePort.increaseBalanceAtomic(wallet.getWalletUuid(), depositAmount)).thenReturn(1);
        when(walletPersistencePort.findByWalletUuid(wallet.getWalletUuid())).thenReturn(Optional.of(wallet));
        when(walletPersistencePort.saveWallet(any(Wallet.class))).thenReturn(wallet);
        when(walletMapper.toResponse(wallet)).thenReturn(walletDepositResponseDto);

        //when
        WalletResponseDto result = walletApplicationService.deposit(customer.getCustomerUuid(), wallet.getWalletUuid(), depositAmount);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(wallet.getBalance().add(depositAmount));

        verify(walletPersistencePort,times(1)).saveWallet(wallet);
    }

    @Test
    void withdrawal() {
        BigDecimal withdrawAmount = new BigDecimal(8000);


        WalletResponseDto walletWithdrawResponseDto = WalletResponseDto
                .builder()
                .walletUuid(wallet.getWalletUuid())
                .customerUuid(customer.getCustomerUuid())
                .balance(wallet.getBalance().subtract(withdrawAmount))
                .build();

        when(walletPersistencePort.findByCustomerUuidAndWalletUuid(customer.getCustomerUuid(), wallet.getWalletUuid())).thenReturn(Optional.of(wallet));
        when(walletPersistencePort.decreaseBalanceAtomic(wallet.getWalletUuid(), withdrawAmount)).thenReturn(1);
        when(walletPersistencePort.findByWalletUuid(wallet.getWalletUuid())).thenReturn(Optional.of(wallet));
        when(walletPersistencePort.saveWallet(any(Wallet.class))).thenReturn(wallet);
        when(walletMapper.toResponse(wallet)).thenReturn(walletWithdrawResponseDto);

        //when
        WalletResponseDto result = walletApplicationService.withdrawal(customer.getCustomerUuid(), wallet.getWalletUuid(), withdrawAmount);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(wallet.getBalance().subtract(withdrawAmount));

        verify(walletPersistencePort,times(1)).saveWallet(wallet);
    }

    @Test
    void freezeWallet() {
        //given
        User admin = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();

        WalletFreezeRequest request = WalletFreezeRequest.builder()
                .reason("불법계정")
                .build();

        WalletResponseDto walletFreezeResponse = WalletResponseDto.builder()
                .walletUuid(wallet.getWalletUuid())
                .customerUuid(customer.getCustomerUuid()).walletStatus(WalletStatus.FROZEN)
                .build();


        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(walletPersistencePort.findByWalletUuid(wallet.getWalletUuid())).thenReturn(Optional.of(wallet));
        when(walletPersistencePort.saveWallet(any(Wallet.class))).thenReturn(wallet);
        when(walletMapper.toResponse(wallet)).thenReturn(walletFreezeResponse);

        //when
        WalletResponseDto result = walletApplicationService.freezeWallet(admin.getUserUuid(), wallet.getWalletUuid(), request);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getWalletStatus()).isEqualTo(WalletStatus.FROZEN);

        verify(walletPersistencePort,times(1)).saveWallet(wallet);

    }

    @Test
    void unFreezeWallet() {
        //given
        User admin = User.builder().userUuid(UUID.randomUUID()).userRole(UserRole.ADMIN).build();

        WalletResponseDto walletUnFreezeResponse = WalletResponseDto.builder()
                .walletUuid(wallet.getWalletUuid())
                .customerUuid(customer.getCustomerUuid()).walletStatus(WalletStatus.ACTIVE)
                .build();

        when(userPersistencePort.findByUserUuid(admin.getUserUuid())).thenReturn(Optional.of(admin));
        when(walletPersistencePort.findByWalletUuid(wallet.getWalletUuid())).thenReturn(Optional.of(wallet));
        when(walletPersistencePort.saveWallet(any(Wallet.class))).thenReturn(wallet);
        when(walletMapper.toResponse(wallet)).thenReturn(walletUnFreezeResponse);

        //when
        WalletResponseDto result = walletApplicationService.unFreezeWallet(admin.getUserUuid(), wallet.getWalletUuid());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getWalletStatus()).isEqualTo(WalletStatus.ACTIVE);

        verify(walletPersistencePort,times(1)).saveWallet(wallet);


    }
}