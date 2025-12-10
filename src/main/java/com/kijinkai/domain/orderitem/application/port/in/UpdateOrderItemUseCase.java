package com.kijinkai.domain.orderitem.application.port.in;

import com.kijinkai.domain.orderitem.application.dto.OrderItemApprovalRequestDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UpdateOrderItemUseCase {

    OrderItem updateOrderItemWithValidate(UUID userUuid, UUID orderUuid, OrderItemUpdateDto updateDto);

    OrderItem updateOrderItemByAdmin(UUID userUuid, UUID orderUuid, OrderItemUpdateDto updateDto);

    //관리자가 구매승인
    List<OrderItemResponseDto> approveOrderItemByAdmin(UUID userUuid, OrderItemApprovalRequestDto requestDto);

    // 구매자의 결제 list, status 변경
    List<OrderItem> firstOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID productPaymentUuid);

}
