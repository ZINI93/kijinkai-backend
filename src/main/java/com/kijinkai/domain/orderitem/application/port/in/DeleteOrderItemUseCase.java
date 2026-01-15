package com.kijinkai.domain.orderitem.application.port.in;

import java.util.UUID;

public interface DeleteOrderItemUseCase {
    void deleteOrderItem(UUID orderItemUuid);
    void deleteByOrderItemCode(UUID userUuid, String orderItemCode);
}
