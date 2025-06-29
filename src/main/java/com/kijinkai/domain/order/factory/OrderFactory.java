package com.kijinkai.domain.order.factory;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.entity.OrderStatus;
import com.kijinkai.domain.payment.entity.PaymentType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

import static com.kijinkai.domain.exchange.doamin.Currency.JPY;


@Component
public class OrderFactory {

    public Order createOrder(Customer customer, String memo) {
        return Order.builder()
                .orderUuid(UUID.randomUUID())
                .customer(customer)
                .totalPriceOriginal(BigDecimal.ZERO)
                .totalPriceConverted(BigDecimal.ZERO)
                .convertedCurrency(JPY)
                .orderStatus(OrderStatus.DRAFT)
                .rejectedReason(null)
                .memo(memo)
                .paymentType(PaymentType.CREDIT)
                .build();
    }
}