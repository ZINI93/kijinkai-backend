package com.kijinkai.domain.orderitem.application.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class OrderItemCountResponseDto {

    int allOrderItemCount;
    int pendingCount;
    int pendingApprovalCount;
}
