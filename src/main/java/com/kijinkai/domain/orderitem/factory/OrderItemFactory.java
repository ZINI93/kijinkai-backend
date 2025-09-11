package com.kijinkai.domain.orderitem.factory;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class OrderItemFactory {

    public OrderItem createOrderItem(Customer customer, Order order, BigDecimal convertedPrice, OrderItemRequestDto requestDto) {

        return OrderItem.builder()
                .orderItemUuid(UUID.randomUUID())
                .customerUuid(customer.getCustomerUuid())
                .order(order)
                .productLink(requestDto.getProductLink())
                .quantity(requestDto.getQuantity())
                .priceOriginal(requestDto.getPriceOriginal())
                .priceConverted(convertedPrice)
                .currencyOriginal(Currency.JPY)
                .currencyConverted(requestDto.getCurrencyConverted())
                .orderItemStatus(OrderItemStatus.PENDING)
                .memo(requestDto.getMemo())
                .build();

    }
}
