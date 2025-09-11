package com.kijinkai.domain.orderitem.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class OrderItemCountResponseDto {

    int allOrderItemCount;
    int pendingCount;
    int pendingApprovalCount;
}
