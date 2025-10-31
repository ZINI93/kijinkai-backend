package com.kijinkai.domain.payment.domain.model;

import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class OrderPaymentTest {


    @Test
    @DisplayName("결제 완료")
    void complete(){
        //given
        OrderPayment orderpayment = OrderPayment
                .builder()
                .orderPaymentStatus(OrderPaymentStatus.PENDING)
                .paymentAmount(new BigDecimal(1000))
                .build();
        //when
        orderpayment.complete();

        //then
        assertThat(orderpayment.getOrderPaymentStatus()).isEqualTo(OrderPaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("대기 상태 검증")
    void isPending(){
        //given
        OrderPayment orderpayment = OrderPayment
                .builder()
                .orderPaymentStatus(OrderPaymentStatus.PENDING)
                .build();

        //when //then
        assertThat(orderpayment.isPending()).isTrue();
    }

    @Test
    @DisplayName("대기 상태 검증")
    void isCompleted(){
        //given
        OrderPayment orderpayment = OrderPayment
                .builder()
                .orderPaymentStatus(OrderPaymentStatus.COMPLETED)
                .build();

        //when //then
        assertThat(orderpayment.isCompleted()).isTrue();
    }



    @Test
    @DisplayName("완료 상태에서 실패했을떄 예외")
    void markAsFailed(){

        String reason = "실패";

        //given
        OrderPayment orderpayment = OrderPayment
                .builder()
                .orderPaymentStatus(OrderPaymentStatus.COMPLETED)
                .build();
        //when //then
        assertThatThrownBy(
                () ->
                {orderpayment.markAsFailed(reason);})
                .isInstanceOf(IllegalStateException.class)
                .hasMessageMatching("Completed request cannot fail");
    }


    @Test
    @DisplayName("결제 실패 처리")
    void markAsFailed2(){

        String reason = "실패";

        //given
        OrderPayment orderpayment = OrderPayment
                .builder()
                .orderPaymentStatus(OrderPaymentStatus.PENDING)
                .build();
        //when
        orderpayment.markAsFailed(reason);

        // then
        assertThat(orderpayment.getOrderPaymentStatus()).isEqualTo(OrderPaymentStatus.FAILED);
        assertThat(orderpayment.getRejectReason()).isEqualTo(reason);

    }

    @Test
    @DisplayName("금액 업데이트")
    void updateTotalAmount(){
        //given

        BigDecimal updateAmount = new BigDecimal(2000.00);


        OrderPayment orderpayment = OrderPayment
                .builder()
                .paymentAmount(new BigDecimal(1000.00))
                .build();
        //when
        orderpayment.updateTotalAmount(updateAmount);

        //then
        assertThat(orderpayment.getPaymentAmount()).isEqualTo(updateAmount);
    }
}