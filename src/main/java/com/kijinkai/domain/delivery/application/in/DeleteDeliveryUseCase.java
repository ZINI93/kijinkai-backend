package com.kijinkai.domain.delivery.application.in;

import java.util.UUID;

public interface DeleteDeliveryUseCase {
    void deleteDelivery(UUID userUuid, UUID deliveryUuid);
}
