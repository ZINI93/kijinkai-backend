package com.kijinkai.domain.delivery.application.in;

import com.kijinkai.domain.delivery.application.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryUpdateDto;

import java.util.UUID;

public interface UpdateDeliveryUseCase {
    DeliveryResponseDto updateDeliveryWithValidate(UUID userUuid, UUID deliveryUuid, DeliveryUpdateDto updateDto);
    DeliveryResponseDto shipDelivery(UUID userUuid, UUID deliveryUuid);
}
