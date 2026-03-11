package com.kijinkai.domain.orderitem.domain.model;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.InspectionStatus;
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

    //식별자
    private Long orderItemId;
    private UUID orderItemUuid;
    private UUID customerUuid;
    private UUID deliveryUuid;
    private UUID shipmentUuid;
    private UUID orderUuid;
    private UUID productPaymentUuid;
    private UUID deliveryFeePaymentUuid;


    //외부식별자
    private String orderItemCode;
    private String productLink;
    private int quantity;
    private BigDecimal priceOriginal;
    private Currency currencyOriginal; //JYP
    private String memo;
    private OrderItemStatus orderItemStatus;
    private String rejectReason;

    private InspectionStatus inspectionStatus;
    private LocalDateTime inspectedAt;

    // 주문 당시 스냅샷
    private BigDecimal appliedRate;
    private BigDecimal discountAmount;
    private BigDecimal paidAmount;

    private LocalDateTime localArrivedAt;
    // 메타 데이터
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


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


    private void changeStatusToPendingApproval() {
        this.orderItemStatus = OrderItemStatus.PENDING_APPROVAL;
    }

    public void isCancel() {
        if (this.orderItemStatus != OrderItemStatus.PRODUCT_PAYMENT_COMPLETED) {
            throw new OrderItemValidateException("결제된 상품만 취소가 가능합니다.");
        }

        orderItemStatus = OrderItemStatus.CANCELLED;
    }


    public void markAsDeliveryPaymentRequest(UUID deliveryFeePaymentUuid) {
        this.deliveryFeePaymentUuid = deliveryFeePaymentUuid;
        this.orderItemStatus = OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST;
    }

    // 추가


    public void addOrderUuid(UUID orderUuid) {
        this.orderUuid = orderUuid;
    }

    public void addDeliveryUuid(UUID deliveryUuid) {
        this.deliveryUuid = deliveryUuid;
    }

    public void addShipmentUuid(UUID shipmentUuid) {
        this.shipmentUuid = shipmentUuid;
    }

    // 상태변경.



    public void changeLocalDeliveryCompleted() {
        if (this.orderItemStatus != OrderItemStatus.LOCAL_ORDER_COMPLETED) {
            throw new OrderItemValidateException("현지 주문된 상품만 현지 도착으로 변경 할 수 있습니다.");
        }

        if (this.inspectionStatus == InspectionStatus.READY) {
            throw new OrderItemValidateException("사진을 검수 해주세요.");
        }

        this.localArrivedAt = LocalDateTime.now();
        this.orderItemStatus = OrderItemStatus.LOCAL_DELIVERY_COMPLETED;
    }

    public void completeInspection() {
        if (this.inspectionStatus != InspectionStatus.READY) {
            throw new OrderItemValidateException("검수 대기상태만 완료가 가능합니다.");
        }



        this.inspectedAt = LocalDateTime.now();
        this.inspectionStatus = InspectionStatus.COMPLETED;
    }

    public void changeLocalOrderCompleted() {
        if (this.orderItemStatus != OrderItemStatus.PRODUCT_PAYMENT_COMPLETED) {
            throw new OrderItemValidateException("결제가 완료된 상품만 로컬에서 주문할수 있습니다.");
        }

        this.orderItemStatus = OrderItemStatus.LOCAL_ORDER_COMPLETED;
    }

    public void requestPhotoInspection() {
        if (this.inspectionStatus != InspectionStatus.NONE) {
            throw new OrderItemValidateException("비검수 대상만, 검수대기로 변경이 가능합니다.");
        }

        this.inspectionStatus = InspectionStatus.READY;

    }


    public void reject(String rejectReason) {
        if (this.orderItemStatus != OrderItemStatus.PENDING) {
            throw new OrderItemValidateException("대기 중 상품만 승인이 가능합니다.");
        }

        this.orderItemStatus = OrderItemStatus.REJECTED;
        this.rejectReason = rejectReason;
    }

    public void delivered() {
        if (this.orderItemStatus != OrderItemStatus.IN_TRANSIT) {
            throw new OrderItemValidateException("국제 배송중 상태에서만 완료가 됩니다.");
        }
        this.orderItemStatus = OrderItemStatus.DELIVERED;
    }

    public void startDelivery() {
        if (this.orderItemStatus != OrderItemStatus.DELIVERY_FEE_PAYMENT_COMPLETED) {
            throw new OrderItemValidateException("결제된 상품만 배송이 가능합니다.");
        }

        this.orderItemStatus = OrderItemStatus.IN_TRANSIT;

    }

    public void completedDeliveryPayment() {
        if (this.orderItemStatus != OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST) {
            throw new OrderItemValidateException("배송비 결제요청 대기중 상품만 결제가 가능합니다.");
        }

        this.orderItemStatus = OrderItemStatus.DELIVERY_FEE_PAYMENT_COMPLETED;
    }

    public void exceedStoragePeriod() {
        if (this.orderItemStatus != OrderItemStatus.LOCAL_DELIVERY_COMPLETED) {
            throw new OrderItemValidateException("현지 창고로 도착하지 않은 상품은 통합할수 없습니다.");
        }

        this.rejectReason = "창고 보관기간 30일 초과";
        this.orderItemStatus = OrderItemStatus.CANCELLED;
    }


    public void startConsolidation() {
        if (this.orderItemStatus != OrderItemStatus.LOCAL_ORDER_COMPLETED) {
            throw new OrderItemValidateException("대기중인 상품만 통합으로 변경가능 합니다. ");
        }

        this.orderItemStatus = OrderItemStatus.PRODUCT_CONSOLIDATING;
    }

    public void approveOrderItem(BigDecimal price, int quantity) {
        if (this.orderItemStatus != OrderItemStatus.PENDING) {
            throw new OrderItemValidateException("대기 중 상품만 승인이 가능합니다.");
        }

        BigDecimal finalPrice = (price != null) ? price : this.getPriceOriginal();
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderItemValidateException("금액은 0엔 보다 이상이여야 합니다.");
        }

        if (quantity <= 0) {
            throw new OrderItemValidateException("수량은 1개 보다 이상이여야 합니다.");
        }

        this.priceOriginal = finalPrice;
        this.quantity = quantity;

        changeStatusToPendingApproval();
    }

    public void completedLocalDelivery() {
        if (this.orderItemStatus != OrderItemStatus.PRODUCT_PAYMENT_COMPLETED) {
            throw new OrderItemValidateException("대기 중 상품만 승인 또는 거절이 가능합니다.");
        }

        // 주문 상품의 상태를 허가로 변경
        this.localArrivedAt = LocalDateTime.now();
        this.orderItemStatus = OrderItemStatus.LOCAL_DELIVERY_COMPLETED;
    }

    public void completeFirstOrderItemPayment(BigDecimal appliedRate, BigDecimal paidAmount, BigDecimal discountAmount) {
        if (this.orderItemStatus != OrderItemStatus.PENDING_APPROVAL) {
            throw new OrderItemValidateException("요청 대기중 상품만 결제 가능합니다.");
        }

        if (appliedRate == null || appliedRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderItemValidateException("적용 환율은 0보다 커야 합니다.");
        }

        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderItemValidateException("결제 금액은 0원보다 커야 합니다.");
        }

        this.discountAmount = discountAmount;
        this.appliedRate = appliedRate;
        this.paidAmount = paidAmount;
        this.orderItemStatus = OrderItemStatus.PRODUCT_PAYMENT_COMPLETED;
    }

    public void changeDeliveryFeeRequest() {
        if (this.orderItemStatus != OrderItemStatus.PRODUCT_CONSOLIDATING) {
            throw new OrderItemValidateException("통합 대기중만 변경이 가능합니다.");
        }
        this.orderItemStatus = OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST;
    }

}
