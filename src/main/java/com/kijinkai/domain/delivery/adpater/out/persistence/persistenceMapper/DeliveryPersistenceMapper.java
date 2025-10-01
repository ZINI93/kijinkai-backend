package com.kijinkai.domain.delivery.adpater.out.persistence.persistenceMapper;

import com.kijinkai.domain.delivery.adpater.out.persistence.entity.DeliveryJpaEntity;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryPersistenceMapper {

    Delivery toDelivery(DeliveryJpaEntity deliveryJpaEntity);
    DeliveryJpaEntity toDeliveryJpaEntity(Delivery delivery);

    List<Delivery> toDeliveries(List<DeliveryJpaEntity> deliveryJpaEntities);
    List<DeliveryJpaEntity> toDeliveryJpaEntities(List<Delivery> deliveries);

}
