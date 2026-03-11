package com.kijinkai.domain.delivery.application.in;

import com.kijinkai.domain.delivery.application.dto.request.DeliveryCancelRequestDto;
import com.kijinkai.domain.delivery.application.dto.response.DeliveryResponseDto;
import com.kijinkai.domain.delivery.application.dto.request.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.domain.model.Delivery;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateDeliveryUseCase {
    DeliveryResponseDto updateDeliveryWithValidate(UUID userUuid, UUID deliveryUuid, DeliveryUpdateDto updateDto);
    void completedPacking(UUID deliveryUuid);
    void revertToPending(UUID deliveryUuid);
    void updateTotalShipmentFee(UUID deliveryUuid, BigDecimal totalShipmentFee);
    void shippedDelivery(UUID deliveryUuid);

    DeliveryResponseDto restoreDelivery(UUID userAdminUuid, UUID deliveryUuid);
    DeliveryResponseDto shipDelivery(UUID userUuid, UUID deliveryUuid);
    DeliveryResponseDto cancelDelivery(UUID userAdminUuid, UUID deliveryUuid, DeliveryCancelRequestDto requestDto);
    DeliveryResponseDto requestDeliveryPayment(UUID userAdminUuid, UUID deliveryUuid);

}
