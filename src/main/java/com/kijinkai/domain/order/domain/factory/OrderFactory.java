package com.kijinkai.domain.order.domain.factory;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

import static com.kijinkai.domain.exchange.doamin.Currency.JPY;


@Component
public class OrderFactory {

    public Order createOrder(UUID customerUuid, BigDecimal totalPrice, String orderCode) {
        return Order.builder()
                .orderUuid(UUID.randomUUID())
                .customerUuid(customerUuid)
                .orderCode(orderCode)
                .totalPriceOriginal(totalPrice)
                .orderStatus(OrderStatus.FIRST_PAID)
                .paymentType(PaymentType.DEPOSIT)
                .isReviewed(false)
                .build();
    }
}