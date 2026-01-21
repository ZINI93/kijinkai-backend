package com.kijinkai.domain.delivery.adpater.out.persistence;

import com.kijinkai.domain.delivery.adpater.out.persistence.entity.DeliveryJpaEntity;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.adpater.out.persistence.persistenceMapper.DeliveryPersistenceMapper;
import com.kijinkai.domain.delivery.adpater.out.persistence.repository.DeliveryRepository;
import com.kijinkai.domain.delivery.application.out.DeliveryPersistencePort;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryPersistenceAdapter implements DeliveryPersistencePort {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersistenceMapper deliveryPersistenceMapper;

    @Override
    public Delivery saveDelivery(Delivery delivery) {
        DeliveryJpaEntity deliveryJpaEntity = deliveryPersistenceMapper.toDeliveryJpaEntity(delivery);
        deliveryJpaEntity = deliveryRepository.save(deliveryJpaEntity);
        return deliveryPersistenceMapper.toDelivery(deliveryJpaEntity);
    }

    @Override
    public void deleteDelivery(Delivery delivery) {
        DeliveryJpaEntity deliveryJpaEntity = deliveryPersistenceMapper.toDeliveryJpaEntity(delivery);
        deliveryRepository.delete(deliveryJpaEntity);
    }

    @Override
    public Optional<Delivery> findByCustomerUuidAndDeliveryUuid(UUID customerUuid, UUID deliveryUuid) {
        return deliveryRepository.findByCustomerUuidAndDeliveryUuid(customerUuid,deliveryUuid)
                .map(deliveryPersistenceMapper::toDelivery);
    }

    @Override
    public Page<Delivery> findByCustomerUuidByStatus(UUID customerUuid, DeliveryStatus deliveryStatus, Pageable page) {
        return deliveryRepository.findByCustomerUuidByStatus(customerUuid, deliveryStatus,page)
                .map(deliveryPersistenceMapper::toDelivery);
    }

    @Override
    public Page<Delivery> findAllByDeliveryStatus(DeliveryStatus status, Pageable pageable) {
        return deliveryRepository.findAllByDeliveryStatus(status, pageable)
                .map(deliveryPersistenceMapper::toDelivery);
    }

    @Override
    public int findByDeliveryStatusCount(UUID customerUuid, DeliveryStatus deliveryStatus) {
        return deliveryRepository.findByDeliveryStatusCount(customerUuid, deliveryStatus);
    }
}
