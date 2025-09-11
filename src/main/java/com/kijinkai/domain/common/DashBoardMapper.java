package com.kijinkai.domain.common;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DashBoardMapper {


    public DashBoardCountResponseDto getDashboardCountResponse(
            BigDecimal balance, int shippedCount, int deliveredCount,
            int firstCompleted, int secondPending, int secondCompleted,
            int allOrderItemCount, int pendingCount, int pendingApprovalCount,
            int orderItemCompletedCount

    ) {

        return DashBoardCountResponseDto.builder()
                .balance(balance)
                .shippedCount(shippedCount)
                .deliveredCount(deliveredCount)
                .firstCompleted(firstCompleted)
                .secondPending(secondPending)
                .secondCompleted(secondCompleted)
                .allOrderItemCount(allOrderItemCount)
                .pendingCount(pendingCount)
                .pendingApprovalCount(pendingApprovalCount)
                .orderItemCompletedCount(orderItemCompletedCount)
                .build();
    }


}
