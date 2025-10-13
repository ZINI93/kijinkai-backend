package com.kijinkai.domain.orderitem.domain.model;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemValidateException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
public class OrderItem {

    private Long orderItemId;
    private Order order;

    private UUID orderItemUuid;
    private UUID customerUuid;
    private UUID productPaymentUuid;
    private UUID deliveryFeePaymentUuid;

    private String productLink;
    private int quantity;
    private BigDecimal priceOriginal;
    private BigDecimal priceConverted;
    private Currency currencyOriginal; //JYP
    private Currency currencyConverted;
    private BigDecimal exchangeRate;
    private String memo;
    private OrderItemStatus orderItemStatus;


    public void updateOrderItem(OrderItemUpdateDto updateDto) {
        this.productLink = updateDto.getProductLink();
        this.quantity = updateDto.getQuantity();
        this.memo = updateDto.getMemo();
        this.priceOriginal = updateDto.getPriceOriginal();
    }


    public void validateOrderAndOrderItem(Order order) {

        if (!Objects.equals(this.getOrder().getOrderUuid(), order.getOrderUuid())) {
            throw new OrderItemValidateException("OrderItem " + this.getOrderItemUuid() + " does not belong to OrderJpaEntity " + this.order.getOrderUuid());
        }
    }

    public void updateEstimatedPrice() {
        if (this.priceOriginal.compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderItemValidateException("Estimated price cannot be negative for order item: " + this.orderItemUuid);
        }
    }

    public void isCancel(){
        orderItemStatus = OrderItemStatus.CANCELLED;
    }

    public void markAsPaymentCompleted(UUID fristproductPaymentUuid){
        this.productPaymentUuid = fristproductPaymentUuid;
        orderItemStatus = OrderItemStatus.PRODUCT_PAYMENT_COMPLETED;
    }

    public void markAsDeliveryPaymentRequest(UUID deliveryFeePaymentUuid){
        this.deliveryFeePaymentUuid = deliveryFeePaymentUuid;
        this.orderItemStatus = OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST;
    }
}
