package com.kijinkai.domain.order.adapter.out.persistence.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface CustomerOrderSummary {

    UUID getCustomerUuid();
    BigDecimal getTotalAmount();
    Long getOrderCount();
}
