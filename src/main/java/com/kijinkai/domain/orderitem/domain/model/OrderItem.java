package com.kijinkai.domain.orderitem.domain.model;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemValidateException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
public class OrderItem {

    //내부식별
    private Long orderItemId;
    private UUID orderItemUuid;
    private UUID customerUuid;

    //외부식별자
    private String orderItemCode;

    private UUID orderUuid;
    private UUID productPaymentUuid;
    private UUID deliveryFeePaymentUuid;
    private String productLink;
    private int quantity;
    private BigDecimal priceOriginal;
    private Currency currencyOriginal; //JYP
    private String memo;
    private OrderItemStatus orderItemStatus;

    private Boolean inspectionRequested;

    private LocalDateTime createdAt;


    private static final BigDecimal INSPECTION_FEE = BigDecimal.valueOf(300);


    public void addOrderUuid(UUID orderUuid) {
        this.orderUuid = orderUuid;
    }

    public BigDecimal calculateFinalPrice() {
        if (inspectionRequested) {
            return priceOriginal.add(INSPECTION_FEE); // 사진검수 300엔 추가
        }
        return priceOriginal;
    }

    public void updateOrderItem(OrderItemUpdateDto updateDto) {
        this.productLink = updateDto.getProductLink();
        this.quantity = updateDto.getQuantity();
        this.memo = updateDto.getMemo();
        this.priceOriginal = updateDto.getPriceOriginal();
    }


    public void validateOrderAndOrderItem(Order order) {

        if (!Objects.equals(this.orderUuid, order.getOrderUuid())) {
            throw new OrderItemValidateException("OrderItem " + this.getOrderItemUuid() + " does not belong to OrderJpaEntity " + this.orderUuid);
        }
    }

    public void updateEstimatedPrice() {
        if (this.priceOriginal.compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderItemValidateException("Estimated price cannot be negative for order item: " + this.orderItemUuid);
        }
    }

    public void approveOrderItem() {
        if (this.orderItemStatus != OrderItemStatus.PENDING) {
            throw new OrderItemValidateException("대기 중 상품만 승인이 가능합니다.");
        }
        // 주문 상품의 상태를 허가로 변경
        changeStatusToPendingApproval();
    }

    public void completedLocalDelivery() {
        if (this.orderItemStatus != OrderItemStatus.PRODUCT_PAYMENT_COMPLETED) {
            throw new OrderItemValidateException("대기 중 상품만 승인이 가능합니다.");
        }
        // 주문 상품의 상태를 허가로 변경
        this.orderItemStatus = OrderItemStatus.LOCAL_DELIVERY_COMPLETED;
    }



        public void completeFirstOrderItemPayment() {
        if (this.orderItemStatus != OrderItemStatus.PENDING_APPROVAL) {
            throw new OrderItemValidateException("요청 대기중 상품만 결제 가능합니다.");
        }
        this.orderItemStatus = OrderItemStatus.PRODUCT_PAYMENT_COMPLETED;
    }

    public void requestPhotoInspection() {
        if (this.orderItemStatus != OrderItemStatus.PENDING_APPROVAL) {
            throw new OrderItemValidateException("요청 대기중 상품만 결제 가능합니다.");
        }

        this.inspectionRequested = true;

    }

    private void changeStatusToPendingApproval() {
        this.orderItemStatus = OrderItemStatus.PENDING_APPROVAL;
    }

    public void isCancel() {
        orderItemStatus = OrderItemStatus.CANCELLED;
    }

    public void markAsPaymentCompleted(UUID fristproductPaymentUuid) {
        this.productPaymentUuid = fristproductPaymentUuid;
    }


    public void changeStatusToProductPaymentCompleted() {
        this.orderItemStatus = OrderItemStatus.PRODUCT_PAYMENT_COMPLETED;
    }

    public void markAsDeliveryPaymentRequest(UUID deliveryFeePaymentUuid) {
        this.deliveryFeePaymentUuid = deliveryFeePaymentUuid;
        this.orderItemStatus = OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST;
    }
}
