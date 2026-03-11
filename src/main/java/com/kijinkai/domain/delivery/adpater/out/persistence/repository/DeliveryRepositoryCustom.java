package com.kijinkai.domain.delivery.adpater.out.persistence.repository;

import com.kijinkai.domain.delivery.adpater.out.persistence.entity.DeliveryJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DeliveryRepositoryCustom {

    Page<DeliveryJpaEntity> searchDeliveries(DeliverySearchCondition condition, Pageable pageable);
}
