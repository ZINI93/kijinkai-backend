package com.kijinkai.domain.delivery.application.validator;

import com.kijinkai.domain.delivery.adpater.out.persistence.entity.DeliveryJpaEntity;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import com.kijinkai.domain.order.domain.exception.OrderStatusException;
import org.springframework.stereotype.Component;

@Component
public class DeliveryValidator {

    public void requireCancelableStatus(DeliveryJpaEntity delivery) {
        if (delivery.getDeliveryStatus() == DeliveryStatus.CANCELLED){
            throw new OrderStatusException("delivery cannot be cancelled; it must be in CANCELLED status.");
        }
        if (delivery.getDeliveryStatus() == DeliveryStatus.DELIVERED){
            throw new OrderStatusException("delivered items cannot be cancelled");
        }
    }

    public void requirePendingStatus(Delivery delivery) {
        if (delivery.getDeliveryStatus() != DeliveryStatus.PENDING){
            throw new OrderStatusException("delivery cannot be shipped; it must be in pending status.");
        }
    }
}
