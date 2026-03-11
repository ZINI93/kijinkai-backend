package com.kijinkai.domain.delivery.application.in;

import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.application.dto.response.DeliveryCountResponseDto;
import com.kijinkai.domain.delivery.application.dto.response.DeliveryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface GetDeliveryUseCase {


    DeliveryResponseDto getDeliveryInfo(UUID userUuid, UUID deliveryUuid);
    Page<DeliveryResponseDto> getDeliveriesByStatus(UUID userUuid, DeliveryStatus deliveryStatus, Pageable pageable);
    DeliveryCountResponseDto getDeliveryDashboardCount(UUID userUuid);
    Page<DeliveryResponseDto> getDeliveriesByAdmin(UUID userAdminUuid, String name, String phoneNumber, DeliveryStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);
    DeliveryResponseDto getRequestDeliveryOrderItemByAdmin(UUID userAdminUui, UUID deliveryUuid, Pageable pageable);

    DeliveryResponseDto getCancelReason(UUID userAdminUuid, UUID deliveryUuid);

    int countDeliveryByStatus(UUID userUuid, DeliveryStatus status);

}
