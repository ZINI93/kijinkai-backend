package com.kijinkai.domain.common.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Builder
@Getter
public class MyPageResponseDto {

    // 유저
    String nickname;

    // 지갑관련
    BigDecimal depositBalance; // 지갑 금액
    BigDecimal availableBalance; // 구매가능 금액
    BigDecimal outstandingBalance; //미결제 금액


    // 배송 / 출고 관련
    int undispatchedOrders;
    int failedOrders;
    int purchaseRequestOrders;
    int purchaseApprovedOrders;
    int firstPaymentCompletedOrders;
    int localDeliveryCompletedOrders;
    int combinedProcessingOrders;
    int secondPaymentRequestedOrders;
    int secondPaymentCompletedOrders;
    int internationalShippingOrders;
    int deliveredOrders;


}
