package com.kijinkai.domain.shipment.repository;

import com.kijinkai.domain.shipment.entity.ShipmentEntity;
import com.kijinkai.domain.shipment.entity.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentEntityRepository extends JpaRepository<ShipmentEntity, Long> {

    Page<ShipmentEntity> findAllByCustomerUuidAndShipmentStatus(UUID customerUuid, ShipmentStatus shipmentStatus, Pageable pageable);

    List<ShipmentEntity> findAllByCustomerUuidAndShipmentStatusAndBoxCodeIn(UUID customerUuid, ShipmentStatus shipmentStatus, List<String> boxCode);

    Optional<ShipmentEntity> findByBoxCodeAndShipmentStatus(String boxCode, ShipmentStatus status);
    Optional<ShipmentEntity> findByCustomerUuidAndBoxCodeAndShipmentStatus(UUID customerUuid, String boxCode, ShipmentStatus status);

    @Query("SELECT COUNT(sm) FROM ShipmentEntity sm WHERE sm.customerUuid = :customerUuid AND sm.shipmentStatus = :shipmentStatus")
    int findShipmentCountByStatus(@Param("customerUuid") UUID customerUuid, @Param("shipmentStatus") ShipmentStatus shipmentStatus);
}