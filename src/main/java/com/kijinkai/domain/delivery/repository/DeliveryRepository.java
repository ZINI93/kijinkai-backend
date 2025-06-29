package com.kijinkai.domain.delivery.repository;

import com.kijinkai.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

  Optional<Delivery> findByCustomerCustomerUuidAndDeliveryUuid(UUID customerUuid, UUID deliveryUuid);
}