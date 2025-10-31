package com.kijinkai.domain.payment.domain.model;

import com.kijinkai.domain.payment.domain.enums.RefundStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RefundRequestTest {


    @Test
    @DisplayName("환불 완료")
    void complete() {
        //given

        String adminMemo = "환불 이유";

        RefundRequest refund = RefundRequest.builder()
                .status(RefundStatus.PROCESSING)
                .refundAmount(new BigDecimal(10000))
                .build();

        //when
        refund.complete(adminMemo);

        //then
        assertThat(refund.getStatus()).isEqualTo(RefundStatus.COMPLETED);
        assertThat(refund.getAdminMemo()).isEqualTo(adminMemo);
    }

    @Test
    @DisplayName("환불 완료")
    void isProcessing() {
        //given

        RefundRequest refund = RefundRequest.builder()
                .status(RefundStatus.PROCESSING)
                .refundAmount(new BigDecimal(10000))
                .build();

        //when //then

        assertThat(refund.isProcessing()).isTrue();
    }
}