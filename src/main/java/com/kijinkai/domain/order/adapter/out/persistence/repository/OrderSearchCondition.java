package com.kijinkai.domain.order.adapter.out.persistence.repository;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class OrderSearchCondition {
    String orderCode;
    String name;
    OrderStatus orderStatus;
    LocalDate startDate;
    LocalDate endDate;
}
