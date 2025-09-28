package com.kijinkai.domain.orderitem.service;


import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.dto.OrderItemCountResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemService {

    OrderItem createOrderItem(Customer customer, Order order, OrderItemRequestDto requestDto);

    OrderItem updateOrderItemWithValidate(UUID userUuid, String orderUuid, OrderItemUpdateDto updateDto);

    OrderItem updateOrderItemByAdmin(UUID userUuid, String orderUuid, OrderItemUpdateDto updateDto);

    OrderItemResponseDto getOrderItemInfo(UUID userUuid, String orderItemUuid);

    OrderItem findOrderItemByOrderItemUuid(UUID orderItemUuid);

    void deleteOrderItem(UUID orderItemUuid);

    //관리자가 구매승인
    Optional<OrderItem> approveOrderItemByAdmin();

    // 구매자의 구매 상품 전체 내역 조회
    Page<OrderItemResponseDto> getOrderItems(UUID userUuid, Pageable pageable);

    // 구매자의 구매요청 대기상태의 리스트 조회
    Page<OrderItemResponseDto> getOrderItemByStatus(UUID userUuid, OrderItemStatus orderItemStatus, Pageable pageable);

    // 구매자의 결제 list, status 변경
    List<OrderItem> firstOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID productPaymentUuid);


    //관리자가 배송비 결제 생성
    List<OrderItem> secondOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID deliveryPaymentUuid);


    OrderItemCountResponseDto orderItemDashboardCount(UUID userUuid);








}
