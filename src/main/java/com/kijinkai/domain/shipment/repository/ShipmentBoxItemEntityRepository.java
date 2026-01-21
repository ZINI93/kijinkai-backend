package com.kijinkai.domain.shipment.repository;

import com.kijinkai.domain.shipment.entity.ShipmentBoxItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShipmentBoxItemEntityRepository extends JpaRepository<ShipmentBoxItemEntity, Long> {

    List<ShipmentBoxItemEntity> findAllByShipmentEntityCustomerUuidAndShipmentEntityBoxCode(UUID customerUuid, String shipmentCode);
    List<ShipmentBoxItemEntity> findAllByShipmentEntityBoxCode(String boxCode);
}