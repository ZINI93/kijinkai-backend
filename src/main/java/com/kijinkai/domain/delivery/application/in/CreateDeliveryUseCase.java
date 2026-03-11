package com.kijinkai.domain.delivery.application.in;

import com.kijinkai.domain.delivery.application.dto.request.DeliveryRequestDto;
import com.kijinkai.domain.delivery.application.dto.response.DeliveryResponseDto;

import java.util.UUID;

public interface CreateDeliveryUseCase {

    UUID requestDelivery(UUID userUuid, UUID addressUuid, DeliveryRequestDto requestDto);
    DeliveryResponseDto createDelivery(UUID userUuid, UUID orderUuid, DeliveryRequestDto requestDto);
}
