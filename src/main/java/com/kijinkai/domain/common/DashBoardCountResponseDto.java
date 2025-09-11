package com.kijinkai.domain.common;


import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;


@Builder
@Value
public class DashBoardCountResponseDto {


    //wallet
    BigDecimal balance;

    // delivery count
    int shippedCount;
    int deliveredCount;


    // orderPayment
    int  firstCompleted;
    int  secondPending;
    int  secondCompleted;


    //orderItem
    int allOrderItemCount;
    int pendingCount;
    int pendingApprovalCount;
    int orderItemCompletedCount;



}
