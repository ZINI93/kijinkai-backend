package com.kijinkai.domain.payment.domain.model;

import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.payment.domain.exception.PaymentAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class DepositRequestTest {

    @Test
    @DisplayName("입금 금액이 음수인 경우 예외")
    void validateDepositAmount(){
        //given
        DepositRequest depositRequest = DepositRequest
                .builder()
                .amountOriginal(new BigDecimal(-1000))
                .build();

        //when //then
        assertThatThrownBy(() ->
                {
                depositRequest.validateDepositAmount();})
                .isInstanceOf(PaymentAmountException.class)
                        .hasMessageMatching("The minimum deposit amount is 1,000 en");
    }


    @Test
    @DisplayName("입금 금액이 한도를 초과할 경우 예외 발생")
    void validateDepositAmount2(){
        //given
        DepositRequest depositRequest = DepositRequest
                .builder()
                .amountOriginal(new BigDecimal(1000001))
                .build();

        //when //then
        assertThatThrownBy(() ->
        {
            depositRequest.validateDepositAmount();})
                .isInstanceOf(PaymentAmountException.class)
                .hasMessageMatching("The maximum deposit amount is 1,000,000 en");
    }


    @Test
    @DisplayName("승인")
    void approve(){
        //given
        UUID adminUuid = UUID.randomUUID();
        String memo = "승인해줘요... 승인해줘요....";

        DepositRequest depositRequest = DepositRequest
                .builder()
                .requestUuid(UUID.randomUUID())
                .status(DepositStatus.PENDING_ADMIN_APPROVAL)
                .expiresAt(LocalDateTime.now().plusDays(2))
                .build();

        //when

        depositRequest.approve(adminUuid,memo);

        //then
        assertThat(depositRequest.getStatus()).isEqualTo(DepositStatus.APPROVED);
        assertThat(depositRequest.getProcessedByAdminUuid()).isEqualTo(adminUuid);
        assertThat(depositRequest.getAdminMemo()).isEqualTo(memo);
    }

    @Test
    @DisplayName("거절")
    void reject(){
        //given
        UUID adminUuid = UUID.randomUUID();
        String memo = "거절해줘요... 거절해줘요....";

        DepositRequest depositRequest = DepositRequest
                .builder()
                .requestUuid(UUID.randomUUID())
                .status(DepositStatus.PENDING_ADMIN_APPROVAL)
                .expiresAt(LocalDateTime.now().plusDays(2))
                .build();
        //when
        depositRequest.reject(adminUuid, memo);

        //then
        assertThat(depositRequest.getStatus()).isEqualTo(DepositStatus.REJECTED);
        assertThat(depositRequest.getProcessedByAdminUuid()).isEqualTo(adminUuid);
        assertThat(depositRequest.getRejectionReason()).isEqualTo(memo);
    }

    @Test
    @DisplayName("만료처리")
    void expire(){
        //given
        DepositRequest depositRequest = DepositRequest
                .builder()
                .requestUuid(UUID.randomUUID())
                .status(DepositStatus.PENDING_ADMIN_APPROVAL)
                .expiresAt(LocalDateTime.now().plusDays(2))
                .build();
        //when
        depositRequest.expire();

        //then
        assertThat(depositRequest.getStatus()).isEqualTo(DepositStatus.EXPIRED);

    }

    @Test
    @DisplayName("완료된 상태의 거절처리 예외 체크")
    void markAsFailed(){

        String rejectionReason = "그냥";

        //given
        DepositRequest depositRequest = DepositRequest
                .builder()
                .requestUuid(UUID.randomUUID())
                .status(DepositStatus.APPROVED)
                .expiresAt(LocalDateTime.now().plusDays(2))
                .build();
        //when
        //then
        assertThatThrownBy(()
        -> {depositRequest.markAsFailed(rejectionReason);})
                .isInstanceOf(IllegalStateException.class)
                .hasMessageMatching("완료된 요청은 실패 처리 될 수 없습니다.");
    }

    @Test
    @DisplayName("거절 처리")
    void markAsFailed2(){


        //given
        String rejectionReason = "그냥";
        DepositRequest depositRequest = DepositRequest
                .builder()
                .requestUuid(UUID.randomUUID())
                .status(DepositStatus.PENDING_ADMIN_APPROVAL)
                .expiresAt(LocalDateTime.now().plusDays(2))
                .build();
        //when
        depositRequest.markAsFailed(rejectionReason);

        //then
        assertThat(depositRequest.getStatus()).isEqualTo(DepositStatus.REJECTED);
        assertThat(depositRequest.getRejectionReason()).isEqualTo(rejectionReason);
    }

    @Test
    @DisplayName("승인")
    void isExpired(){
        //given
        DepositRequest depositRequest = DepositRequest
                .builder()
                .requestUuid(UUID.randomUUID())
                .status(DepositStatus.APPROVED)
                .expiresAt(LocalDateTime.now().plusDays(2))
                .build();

        //when
        //then
        assertThat(depositRequest.isExpired()).isFalse();
    }


}