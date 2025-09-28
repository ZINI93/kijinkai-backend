package com.kijinkai.domain.delivery.repository;

import com.kijinkai.domain.delivery.entity.Delivery;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByCustomerUuidAndDeliveryUuid(UUID customerUuid, UUID deliveryUuid);

    @Query("SELECT dv FROM Delivery dv WHERE dv.customerUuid = :customerUuid AND dv.deliveryStatus = :deliveryStatus")
    Page<Delivery> findByCustomerUuidByStatus(@Param("customerUuid") UUID customerUuid, @Param("deliveryStatus") DeliveryStatus deliveryStatus, Pageable page);


    @Query("SELECT COUNT(dv) FROM Delivery dv WHERE dv.customerUuid = :customerUuid AND dv.deliveryStatus = :deliveryStatus")
    int findByDeliveryStatusCount(@Param("customerUuid") UUID customerUuid, @Param("deliveryStatus")  DeliveryStatus deliveryStatus);
}