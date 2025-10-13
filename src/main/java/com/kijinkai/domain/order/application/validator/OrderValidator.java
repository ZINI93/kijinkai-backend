package com.kijinkai.domain.order.application.validator;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.order.domain.exception.OrderStatusException;
import com.kijinkai.domain.order.domain.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {



    public void validateOrderForAdminRejection(Order order) {
        if (order.getOrderStatus() != OrderStatus.DRAFT) {
            throw new OrderStatusException("Order cannot be rejected as it's no longer in DRAFT status.");
        }
    }

    public void requireAwaitingOrderStatus(Order order){
        if (order.getOrderStatus() != OrderStatus.AWAITING_PAYMENT){
            throw new OrderStatusException("Order must be in AWAITING_PAYMENT status to proceed.");
        }
    }

    public void requireDraftOrderStatus(Order order){
        if (order.getOrderStatus() != OrderStatus.DRAFT){
            throw new OrderStatusException("Order must be in AWAITING_PAYMENT status to proceed.");
        }
    }

    public void requireCancellableStatus(Order order){
        if (order.getOrderStatus() != OrderStatus.AWAITING_PAYMENT){
            throw new OrderStatusException("Order cannot be cancelled; it must be in AWAITING_PAYMENT status.");
        }
    }

    public void requirePaidStatusForConfirmation(Order order){
        if (order.getOrderStatus() != OrderStatus.FIRST_PAID){
            throw new OrderStatusException("Order cannot be confirmed; it must be in FIRST_PAID status.");
        }
    }
}
