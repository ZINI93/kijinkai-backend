package com.kijinkai.domain.delivery.service;

import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryUpdateDto;

public interface DeliveryService {

    DeliveryResponseDto createDeliveryWithValidate(String userUuid, DeliveryRequestDto requestDto);
    DeliveryResponseDto updateDeliveryWithValidate(String userUuid, String deliveryUuid, DeliveryUpdateDto updateDto);
    void deleteDelivery(String userUuid, String deliveryUuid);
    DeliveryResponseDto getDeliveryInfo(String userUuid, String deliveryUuid);
}
