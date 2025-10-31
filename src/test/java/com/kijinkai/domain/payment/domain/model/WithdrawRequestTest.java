package com.kijinkai.domain.payment.domain.model;

import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WithdrawRequestTest {

    @Test
    @DisplayName("승인")
    void approve(){
        //given

        UUID adminUuid  = UUID.randomUUID();
        String memo = "승인";

        WithdrawRequest withdraw = WithdrawRequest.builder()
                .status(WithdrawStatus.PENDING_ADMIN_APPROVAL)
                .requestAmount(new BigDecimal(100000))
                .build();

        //when
        withdraw.approve(adminUuid,memo);

        //then
        assertThat(withdraw.getProcessedByAdminUuid()).isEqualTo(adminUuid);
        assertThat(withdraw.getStatus()).isEqualTo(WithdrawStatus.APPROVED);
        assertThat(withdraw.getAdminMemo()).isEqualTo(memo);
    }

    @Test
    @DisplayName("실패")
    void markAsFailed(){
        //given

        String reason = "실패";

        WithdrawRequest withdraw = WithdrawRequest.builder()
                .status(WithdrawStatus.PENDING_ADMIN_APPROVAL)
                .build();

        //when
        withdraw.markAsFailed(reason);

        //then
        assertThat(withdraw.getStatus()).isEqualTo(WithdrawStatus.FAILED);
        assertThat(withdraw.getRejectionReason()).isEqualTo(reason);
    }



}