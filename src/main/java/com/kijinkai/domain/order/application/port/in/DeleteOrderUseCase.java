
package com.kijinkai.domain.order.application.port.in;

import java.util.UUID;

public interface DeleteOrderUseCase {

    void deleteOrder(UUID userUuid, UUID orderUuid);
}
