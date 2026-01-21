package com.kijinkai.domain.orderitem.application.port.in;

import com.kijinkai.domain.orderitem.application.dto.OrderItemApprovalRequestDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UpdateOrderItemUseCase {

    OrderItem updateOrderItemWithValidate(UUID userUuid, UUID orderUuid, OrderItemUpdateDto updateDto);

    OrderItem updateOrderItemByAdmin(UUID userUuid, UUID orderUuid, OrderItemUpdateDto updateDto);

    //관리자가 구매승인
//    List<OrderItemResponseDto> approveOrderItemByAdmin(UUID userUuid, OrderItemApprovalRequestDto requestDto);

    // 구매자의 결제 list, status 변경
    List<OrderItem> firstOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID productPaymentUuid);

    List<String> processFirstOderItem(UUID userUuid, OrderItemApprovalRequestDto requestDto);

    List<String> completeLocalDelivery(UUID userUuid, OrderItemApprovalRequestDto requestDto);

    void updateOrderItemStatusByFirstComplete(List<OrderItem> orderItems, Map<String, Boolean> inspectionRequestMap);

    void requestPhotoInspection(List<OrderItem> orderItems, Map<String, Boolean> inspectionRequestMap);

    void registerDeliveryToOrderItems(List<String> orderItemCods, UUID deliveryUuid);

    void assignToShipment(List<String> orderItemCodes, UUID shipmentUuid);

    void completedDeliveryPayment(UUID customerUuid, List<UUID> shipmentUuids);

    void startDelivery(UUID shipmentUuid);

    void delivered(UUID shipmentUuid);
}

