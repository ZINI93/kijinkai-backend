package com.kijinkai.domain.orderitem.factory;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.entity.Currency;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.platform.entity.Platform;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class OrderItemFactory {

    public OrderItem createOrderItem(Customer customer, Platform platform, Order order,  OrderItemRequestDto requestDto) {

        return OrderItem.builder()
                .orderItemUuid(UUID.randomUUID().toString())
                .customer(customer)
                .platform(platform)
                .order(order)
                .productLink(requestDto.getProductLink())
                .quantity(requestDto.getQuantity())
                .priceOriginal(requestDto.getPriceOriginal())
                .priceConverted(BigDecimal.ZERO)
                .currencyOriginal(Currency.JPY)
                .currencyConverted(requestDto.getCurrencyConverted())
                .exchangeRate(BigDecimal.ZERO)
                .memo(requestDto.getMemo())
                .build();

    }
}
