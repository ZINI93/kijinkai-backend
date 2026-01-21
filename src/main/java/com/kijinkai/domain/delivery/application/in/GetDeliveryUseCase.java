package com.kijinkai.domain.delivery.application.in;

import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.application.dto.DeliveryCountResponseDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetDeliveryUseCase {


    DeliveryResponseDto getDeliveryInfo(UUID userUuid, UUID deliveryUuid);
    Page<DeliveryResponseDto> getDeliveriesByStatus(UUID userUuid, DeliveryStatus deliveryStatus, Pageable pageable);
    DeliveryCountResponseDto getDeliveryDashboardCount(UUID userUuid);
    Page<DeliveryResponseDto> getDeliveries(UUID userUuid, DeliveryStatus deliveryStatus,Pageable pageable);
}
