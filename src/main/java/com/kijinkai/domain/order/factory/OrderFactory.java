package com.kijinkai.domain.order.factory;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.entity.OrderStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

import static com.kijinkai.domain.exchange.doamin.Currency.JPY;


@Component
public class OrderFactory {

    public Order createOrder(Customer customer, String memo, BigDecimal totalPrice) {
        return Order.builder()
                .orderUuid(UUID.randomUUID())
                .customer(customer)
                .totalPriceOriginal(totalPrice)
                .totalPriceConverted(BigDecimal.ZERO)
                .convertedCurrency(JPY)
                .orderStatus(OrderStatus.DRAFT)
                .memo(memo)
                .paymentType(PaymentType.WITHDRAWAL)
                .build();
    }
}