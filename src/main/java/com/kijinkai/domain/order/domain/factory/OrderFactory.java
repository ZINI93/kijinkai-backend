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

    public Order createOrder(UUID customerUuid, String memo, BigDecimal totalPrice) {
        return Order.builder()
                .orderUuid(UUID.randomUUID())
                .customerUuid(customerUuid)
                .totalPriceOriginal(totalPrice)
                .totalPriceConverted(BigDecimal.ZERO)
                .convertedCurrency(JPY)
                .orderStatus(OrderStatus.DRAFT)
                .memo(memo)
                .paymentType(PaymentType.WITHDRAWAL)
                .build();
    }
}