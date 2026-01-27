package com.kijinkai.domain.orderitem.adapter.out.persistence.repostiory;

import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import lombok.Data;

import java.time.LocalDate;



@Data
public class OrderItemSearchCondition {


    private OrderItemStatus status;
    private String orderItemCode;
    private LocalDate startDate;
    private LocalDate endDate;

}
