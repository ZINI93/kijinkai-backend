package com.kijinkai.domain.order.validator;

import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.entity.OrderStatus;
import com.kijinkai.domain.order.exception.OrderStatusException;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {

//    public void validateCreate(OrderRequestDto dto) {
//        if (dto.getOrderItemUuids() == null || dto.getOrderItemUuids().isEmpty()) {
//            throw new OrderItemInvalidException("INVALID_ORDER_ITEMS");
//        }
//
//        if (dto.getDeliveryFee() == null || dto.getDeliveryFee().compareTo(BigDecimal.ZERO) < 0) {
//            throw new DeliveryInvalidException("INVALID_DELIVERY_FEE");
//        }
//    }
//
//    public void validateAdminApproval(Order order) {
//        if (order.getOrderstate() != OrderState.PENDING) {
//            throw new OrderItemInvalidException("ORDER_ALREADY_PROCESSED");
//        }
//    }

    public void validateOrderForAdminRejection(Order order) {
        if (order.getOrderStatus() != OrderStatus.DRAFT) {
            throw new OrderStatusException("Order cannot be rejected as it's no longer in DRAFT status.");
        }
    }

    public void requireAwaitingPaymentStatus(Order order){
        if (order.getOrderStatus() != OrderStatus.AWAITING_PAYMENT){
            throw new OrderStatusException("Order must be in AWAITING_PAYMENT status to proceed.");
        }
    }

    public void requireCancellableStatus(Order order){
        if (order.getOrderStatus() != OrderStatus.AWAITING_PAYMENT){
            throw new OrderStatusException("Order cannot be cancelled; it must be in AWAITING_PAYMENT status.");
        }
    }

    public void requirePaidStatusForConfirmation(Order order){
        if (order.getOrderStatus() != OrderStatus.PAID ){
            throw new OrderStatusException("Order cannot be confirmed; it must be in PAID status.");
        }
    }
}
