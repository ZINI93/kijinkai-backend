package com.kijinkai.domain.delivery.service;

import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.entity.Delivery;

public interface DeliveryService {

    DeliveryResponseDto createDeliveryWithValidate(String userUuid, String orderUuid, DeliveryRequestDto requestDto);
    DeliveryResponseDto deliveryShipped(String userUuid, String deliveryUuid);

    DeliveryResponseDto updateDeliveryWithValidate(String userUuid, String deliveryUuid, DeliveryUpdateDto updateDto);

    void deleteDelivery(String userUuid, String deliveryUuid);
    DeliveryResponseDto getDeliveryInfo(String userUuid, String deliveryUuid);
}
