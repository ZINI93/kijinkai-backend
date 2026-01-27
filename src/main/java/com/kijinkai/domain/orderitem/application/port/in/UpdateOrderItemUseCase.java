package com.kijinkai.domain.orderitem.application.port.in;

import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.dto.*;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UpdateOrderItemUseCase {

    OrderItem updateOrderItemWithValidate(UUID userUuid, UUID orderUuid, OrderItemUpdateDto updateDto);

    OrderItem updateOrderItemByAdmin(UUID userUuid, UUID orderUuid, OrderItemUpdateDto updateDto);

    List<String> processFirstOderItem(UUID userUuid, OrderItemApprovalRequestDto requestDto);

    List<String> completeLocalDelivery(UUID userUuid, OrderItemApprovalRequestDto requestDto);

    void updateOrderItemStatusByFirstComplete(List<OrderItem> orderItems, Map<String, Boolean> inspectionRequestMap);

    void requestPhotoInspection(List<OrderItem> orderItems, Map<String, Boolean> inspectionRequestMap);

    void registerDeliveryToOrderItems(List<String> orderItemCods, UUID deliveryUuid);

    void assignToShipment(List<String> orderItemCodes, UUID shipmentUuid);

    void completedDeliveryPayment(UUID customerUuid, List<UUID> shipmentUuids);

    void startDelivery(UUID shipmentUuid);

    void delivered(UUID shipmentUuid);
    String rejectOrderItem(UUID userAdminUuid, UUID orderItemUuid, OrderItemRejectRequestDto requestDto);
}

