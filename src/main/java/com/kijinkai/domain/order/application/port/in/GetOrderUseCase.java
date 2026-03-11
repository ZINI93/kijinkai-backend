package com.kijinkai.domain.order.application.port.in;

import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GetOrderUseCase {

    OrderResponseDto getOrderInfo(UUID userUuid, UUID orderUuid);
    List<OrderResponseDto> getPendingReviewOrders(UUID userUuid);
    Page<OrderResponseDto> getOrdersByAdmin(UUID userAdminUuid, String orderCode, String name, OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
