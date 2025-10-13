package com.kijinkai.domain.delivery.adpater.out.persistence.repository;

import com.kijinkai.domain.delivery.adpater.out.persistence.entity.DeliveryJpaEntity;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<DeliveryJpaEntity, Long> {

    Optional<DeliveryJpaEntity> findByCustomerUuidAndDeliveryUuid(UUID customerUuid, UUID deliveryUuid);

    @Query("SELECT dv FROM DeliveryJpaEntity dv WHERE dv.customerUuid = :customerUuid AND dv.deliveryStatus = :deliveryStatus")
    Page<DeliveryJpaEntity> findByCustomerUuidByStatus(@Param("customerUuid") UUID customerUuid, @Param("deliveryStatus") DeliveryStatus deliveryStatus, Pageable page);


    @Query("SELECT COUNT(dv) FROM DeliveryJpaEntity dv WHERE dv.customerUuid = :customerUuid AND dv.deliveryStatus = :deliveryStatus")
    int findByDeliveryStatusCount(@Param("customerUuid") UUID customerUuid, @Param("deliveryStatus")  DeliveryStatus deliveryStatus);
}