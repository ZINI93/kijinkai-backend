package com.kijinkai.domain.orderitem.adapter.out.persistence.repostiory;

import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;



@Data
@Builder
public class OrderItemSearchCondition {

    private String orderItemCode;
    private String name;
    private OrderItemStatus status;
    private LocalDate startDate;
    private LocalDate endDate;

}
