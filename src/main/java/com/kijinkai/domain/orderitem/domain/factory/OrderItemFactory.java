package com.kijinkai.domain.orderitem.domain.factory;


import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.application.dto.OrderItemRequestDto;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class OrderItemFactory {

    public OrderItem createOrderItem(Customer customer, OrderItemRequestDto requestDto, String orderItemCode) {

        return OrderItem.builder()
                .orderItemUuid(UUID.randomUUID())
                .customerUuid(customer.getCustomerUuid())
                .orderItemCode(orderItemCode)
                .productLink(requestDto.getProductLink())
                .quantity(requestDto.getQuantity())
                .priceOriginal(requestDto.getPriceOriginal())
                .currencyOriginal(Currency.JPY)
                .inspectionRequested(false)
                .orderItemStatus(OrderItemStatus.PENDING)
                .memo(requestDto.getMemo())
                .build();

    }
}
