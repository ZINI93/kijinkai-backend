package com.kijinkai.domain.delivery.application.out;


import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryPersistencePort {

    Delivery saveDelivery(Delivery delivery);
    void deleteDelivery(Delivery delivery);
    Optional<Delivery> findByCustomerUuidAndDeliveryUuid(UUID customerUuid, UUID deliveryUuid);
    Page<Delivery> findByCustomerUuidByStatus(@Param("customerUuid") UUID customerUuid, @Param("deliveryStatus") DeliveryStatus deliveryStatus, Pageable page);
    int findByDeliveryStatusCount(@Param("customerUuid") UUID customerUuid, @Param("deliveryStatus")  DeliveryStatus deliveryStatus);
}
