package com.kijinkai.domain.order.application.mapper;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import com.kijinkai.domain.user.domain.model.User;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.UUID;


@Component
public class OrderMapper {

    public OrderResponseDto toResponse(Order order) {

        return OrderResponseDto.builder()
                .orderUuid(order.getOrderUuid())
                .customerUuid(order.getCustomerUuid())
                .totalPriceOriginal(order.getTotalPriceOriginal())
                .memo(order.getMemo())
                .build();
    }


    public OrderResponseDto toAdminOrderListResponse(Order order, User user, Customer customer, Long orderItemCount) {

        return OrderResponseDto.builder()
                .orderUuid(order.getOrderUuid())
                .orderCode(order.getOrderCode())
                .customerUuid(customer.getCustomerUuid())
                .name(customer.getLastName() + customer.getFirstName())
                .orderItemCount(orderItemCount)
                .email(user.getEmail())
                .orderState(order.getOrderStatus())
                .totalPriceOriginal(order.getTotalPriceOriginal())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }



    public OrderResponseDto toOrderResponse(OrderResponseDto order, OrderPayment orderPayment) {

        return OrderResponseDto.builder()
                .orderUuid(order.getOrderUuid())
                .customerUuid(order.getCustomerUuid())
                .totalPriceOriginal(order.getTotalPriceOriginal().setScale(0, RoundingMode.HALF_UP))
                .discountAmount(orderPayment.getDiscountAmount().setScale(0, RoundingMode.HALF_UP))
                .finalAmount(orderPayment.getFinalPaymentAmount().setScale(0, RoundingMode.HALF_UP))
                .memo(order.getMemo())
                .build();
    }


    public OrderResponseDto toReviewResponse(Order order) {

        return OrderResponseDto.builder()
                .orderUuid(order.getOrderUuid())
                .orderCode(order.getOrderCode())
                .build();
    }


}
