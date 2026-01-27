package com.kijinkai.domain.orderitem.application.port.in;

import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.dto.OrderItemCountResponseDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GetOrderItemUseCase {

    // 구매자의 구매 상품 전체 내역 조회
    Page<OrderItemResponseDto> getOrderItems(UUID userUuid, Pageable pageable);

    // 구매자의 구매요청 대기상태의 리스트 조회
    Page<OrderItemResponseDto> getOrderItemByStatus(UUID userUuid, OrderItemStatus orderItemStatus, Pageable pageable);

    Page<OrderItemResponseDto> getAdminOrderItemsByStatus(UUID userAdminUuid, OrderItemStatus status, String orderItemCode, LocalDate startDate, LocalDate endDate, Pageable pageable);

    OrderItemResponseDto getOrderItemInfo(UUID userUuid, UUID orderItemUuid);
    OrderItemCountResponseDto orderItemDashboardCount(UUID userUuid);

    List<OrderItemResponseDto> getOrderItemByDeliveryUuid(UUID userUuid, UUID deliveryUuid);
    List<OrderItem> getOrderItemsByCustomerAndOrderItemsStatus(UUID userUuid, List<OrderItemStatus> orderItemStatuses);
    int countOrderItemsByStatus(UUID userUuid, OrderItemStatus orderItemStatus);
    int countOrderItemByStatusIn(UUID userUuid, List<OrderItemStatus> orderItemStatus);

    List<OrderItem> getOrderItemsByCodeAndStatus(List<String> orderItemCode, OrderItemStatus status);
}
