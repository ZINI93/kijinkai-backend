package com.kijinkai.domain.delivery.application.in;

import com.kijinkai.domain.delivery.application.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryResponseDto;

import java.util.UUID;

public interface CreateDeliveryUseCase {

    DeliveryResponseDto createDelivery(UUID userUuid, UUID orderUuid, DeliveryRequestDto requestDto);
}
