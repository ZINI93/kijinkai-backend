package com.kijinkai.domain.orderitem.application.port.in;

import com.kijinkai.domain.orderitem.application.dto.*;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UpdateOrderItemUseCase {

    OrderItem updateOrderItemWithValidate(UUID userUuid, UUID orderUuid, OrderItemUpdateDto updateDto);

    OrderItem updateOrderItemByAdmin(UUID userUuid, UUID orderUuid, OrderItemUpdateDto updateDto);

    List<String> completeLocalDelivery(UUID userUuid, OrderItemApprovalRequestDto requestDto);

    void updateOrderItemStatusByFirstComplete(List<OrderItem> orderItems, Map<String, Boolean> inspectionRequestMap);

    void requestPhotoInspection(List<OrderItem> orderItems, Map<String, Boolean> inspectionRequestMap);

    OrderItemResponseDto localOrderCompleted(UUID userAdminUuid, UUID orderItemUuid);

    void requestDeliveryPayment(UUID deliveryUuid);

    void refundOrderItem(OrderItem orderItem);

    void registerDeliveryToOrderItems(List<String> orderItemCods, UUID deliveryUuid);

    void assignToShipment(List<String> orderItemCodes, UUID shipmentUuid);

    void completedDeliveryPayment(UUID customerUuid, List<UUID> shipmentUuids);

    void startDelivery(UUID shipmentUuid);

    void processFirstPaymentAndRequestPhotos(List<UUID> requestOrderItemUuids, List<OrderItem> allOrderItems, BigDecimal exchangeRate, Map<UUID, BigDecimal> discountMap);

    void delivered(UUID shipmentUuid);

    OrderItemResponseDto rejectOrderItem(UUID userAdminUuid, UUID orderItemUuid, OrderItemRejectRequestDto requestDto);

    OrderItemResponseDto processFirstOderItem(UUID userAdminUuid, UUID orderItemUuid, OrderItemApprovalRequestDto requestDto);

    OrderItemResponseDto localDeliveryCompleted(UUID userAdminUuid, UUID orderitemUuid);

    OrderItemResponseDto sendInspectionEmail(UUID userAdminUuid, UUID oderItemUuid, List<MultipartFile> photos);

    void processStoragePeriodExceeded(OrderItem orderItem) throws MessagingException;

    void bulkArriveProcess(ArrivedItemRequestDto requestDto);

}

