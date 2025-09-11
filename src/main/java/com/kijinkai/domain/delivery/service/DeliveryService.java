package com.kijinkai.domain.delivery.service;

import com.kijinkai.domain.delivery.dto.DeliveryCountResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.entity.Delivery;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DeliveryService {

    DeliveryResponseDto createDeliveryWithValidate(UUID userUuid, UUID orderUuid, DeliveryRequestDto requestDto);
    DeliveryResponseDto deliveryShipped(UUID userUuid, UUID deliveryUuid);

    DeliveryResponseDto updateDeliveryWithValidate(UUID userUuid, UUID deliveryUuid, DeliveryUpdateDto updateDto);

    void deleteDelivery(UUID userUuid, UUID deliveryUuid);
    DeliveryResponseDto getDeliveryInfo(UUID userUuid, UUID deliveryUuid);

    Page<DeliveryResponseDto> getDeliveriesByStatus(UUID userUuid, DeliveryStatus deliveryStatus, Pageable pageable);

    DeliveryCountResponseDto getDeliveryDashboardCount(UUID userUuid);
}
