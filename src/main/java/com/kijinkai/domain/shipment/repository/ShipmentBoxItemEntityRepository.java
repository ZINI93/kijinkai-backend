package com.kijinkai.domain.shipment.repository;

import com.kijinkai.domain.shipment.entity.ShipmentBoxItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShipmentBoxItemEntityRepository extends JpaRepository<ShipmentBoxItemEntity, Long> {

    List<ShipmentBoxItemEntity> findAllByShipmentEntityCustomerUuidAndShipmentEntityBoxCode(UUID customerUuid, String shipmentCode);
    List<ShipmentBoxItemEntity> findAllByShipmentEntityBoxCode(String boxCode);
    List<ShipmentBoxItemEntity> findAllByShipmentEntityShipmentId(Long shipmentId);

    List<ShipmentBoxItemEntity> findAllByShipmentEntityShipmentIdIn(List<Long> shipmentIds);
    Page<ShipmentBoxItemEntity> findAllByShipmentEntityShipmentIdIn(List<Long> shipmentIds, Pageable pageable);

    void deleteByShipmentEntityShipmentIdIn(List<Long> shipmentIds);
}