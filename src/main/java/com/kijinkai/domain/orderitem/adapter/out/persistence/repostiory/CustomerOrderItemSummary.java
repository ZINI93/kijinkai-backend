package com.kijinkai.domain.orderitem.adapter.out.persistence.repostiory;

import java.util.UUID;

public interface CustomerOrderItemSummary {

    UUID getOrderUuid();
    Long getOrderItemCount();
}
