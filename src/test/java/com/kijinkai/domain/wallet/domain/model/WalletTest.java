package com.kijinkai.domain.wallet.domain.model;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.payment.domain.exception.PaymentAmountException;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletStatus;
import com.kijinkai.domain.wallet.application.dto.WalletFreezeRequest;
import com.kijinkai.domain.wallet.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class WalletTest {


    Customer customer;
    Wallet wallet;

    @BeforeEach
    void setUp(){
        customer = Customer.builder().customerUuid(UUID.randomUUID()).build();

        wallet = Wallet.builder()
                .walletUuid(UUID.randomUUID())
                .customerUuid(this.customer.getCustomerUuid())
                .balance(new BigDecimal(1000.00))
                .walletStatus(WalletStatus.ACTIVE)
                .freezeReason("없음")
                .build();

    }


    @Test
    @DisplayName("계정 동결")
    void freezeWallet(){
        //given
        WalletFreezeRequest request = WalletFreezeRequest.builder().reason("중복계정").build();

        //when
        wallet.unfreeze();

        //then
        assertThat(wallet.getWalletStatus()).isEqualTo(WalletStatus.FROZEN);
        assertThat(wallet.getFreezeReason()).isEqualTo(request.getReason());
    }

    @Test
    @DisplayName("계정 동결 해제")
    void unfreeze(){
        //given


        //when
        wallet.unfreeze();

        //then
        assertThat(wallet.getWalletStatus()).isEqualTo(WalletStatus.ACTIVE);
    }

    @Test
    @DisplayName("계정 활성화 검증")
    void isActive(){
        //given

        //when
        boolean result = wallet.isActive();

        //then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("계정 비활성화 검증")
    void isFrozen(){
        //given

        //when
        boolean result = wallet.isFrozen();

        //then
        assertThat(result).isEqualTo(false);
    }



    @Test
    @DisplayName("계정 비활성화 체크") // 중복체크
    void requireActiveStatus(){
        //given

        //when

        //then
    }

    @Test
    @DisplayName("잔액 부족 검증")
    void FailRequireSufficientBalance(){
        //given
        BigDecimal balance = new BigDecimal(1200);

        //when

        //then
        assertThatThrownBy(() -> {
            wallet.requireSufficientBalance(balance);
        })
                .isInstanceOf(PaymentAmountException.class) // 예상되는 예외 유형 검증
                .hasMessageContaining("Amount must be a positive value");

    }

    @Test
    @DisplayName("최소 금액 체크")
    void validateMinimumExchangeAmount(){
        //given
        // 현재 가진 금액 1000엔

        //when

        //then
        assertThatThrownBy(() ->
        {wallet.validateMinimumExchangeAmount();})
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageMatching("The minimum exchange amount is 8,000 yen");
    }



}