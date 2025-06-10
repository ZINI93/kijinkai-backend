package com.kijinkai.domain.order.dto;

import com.kijinkai.domain.order.entity.OrderStatus;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class OrderUpdateDto {

    private OrderStatus orderstate;
    private String memo;
    private String rejectedReason;
    private List<OrderItemUpdateDto> orderItems;

    public OrderUpdateDto(OrderStatus orderstate, String memo, String rejectedReason) {
        this.orderstate = orderstate;
        this.memo = memo;
        this.rejectedReason = rejectedReason;
    }
}
