package com.kijinkai.domain.orderitem.application.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.kijinkai.domain.order.application.validator.OrderValidator;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.dto.OrderItemCountResponseDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.application.port.in.CreateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.DeleteOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.GetOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.mapper.OrderItemMapper;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.application.validator.OrderItemValidator;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.domain.factory.OrderItemFactory;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderItemApplicationService implements CreateOrderItemUseCase, GetOrderItemUseCase, UpdateOrderItemUseCase, DeleteOrderItemUseCase {

    private final CustomerPersistencePort customerPersistencePort;
    private final OrderItemPersistencePort orderItemPersistencePort;

    private final OrderItemValidator orderItemValidator;
    private final OrderValidator orderValidator;
    private final OrderItemFactory orderItemFactory;
    private final OrderItemMapper orderItemMapper;

    //outer
    private final UserPersistencePort userPersistencePort;
    private final UserApplicationValidator userValidator;
    private final PriceCalculationService priceCalculationService;


    /**
     * 유저로 부터 상품 url, quantity 를 입력 받고, 확인 후 관리자가 가격 수정
     *
     * @param customer
     * @param order
     * @param requestDto
     * @return orderItem
     */
    @Override
    @Transactional
    public OrderItem createOrderItem(Customer customer, Order order, OrderItemRequestDto requestDto) {
        BigDecimal convertedAmount = priceCalculationService.calculateTotalPrice(requestDto.getPriceOriginal());

        return orderItemFactory.createOrderItem(customer, order, convertedAmount, requestDto);
    }

    // 일단 사용하고 리펙토링 할때 위에 코드랑 유사함으로 결합 고려해야함
    @Override
    @Transactional
    public List<OrderItem> secondOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID deliveryPaymentUuid) {
        List<OrderItem> orderItems = orderItemValidator.validateOrderItems(customerUuid, request.getOrderItemUuids(), OrderItemStatus.PRODUCT_PAYMENT_COMPLETED);
        orderItems.forEach(orderItem -> orderItem.markAsDeliveryPaymentRequest(deliveryPaymentUuid));
        return orderItemPersistencePort.saveAllOrderItem(orderItems);
    }

    @Override
    @Transactional
    public void deleteOrderItem(UUID orderItemUuid) {

        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);
        orderItemValidator.requiredPendingStatus(orderItem);
        orderItemPersistencePort.deleteOrderItem(orderItem);
    }

    @Override
    public Page<OrderItemResponseDto> getOrderItems(UUID userUuid, Pageable pageable) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Page<OrderItem> orderItems = orderItemPersistencePort.findAllByCustomerUuidOrderByOrderCreatedAtDesc(customer.getCustomerUuid(), pageable);
        return orderItems.map(orderItemMapper::toResponseDto);
    }

    /**
     * 구매자의 구매요청 중인 상품 목록
     *
     * @param userUuid
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderItemResponseDto> getOrderItemByStatus(UUID userUuid, OrderItemStatus orderItemStatus, Pageable pageable) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Page<OrderItem> orderItems = orderItemPersistencePort.findAllByCustomerUuidAndOrderItemStatusOrderByOrderCreatedAtDesc(customer.getCustomerUuid(), orderItemStatus, pageable);
        return orderItems.map(orderItemMapper::toResponseDto);
    }

    @Override
    public OrderItemResponseDto getOrderItemInfo(UUID userUuid, UUID orderItemUuid) {
        Customer customerJpaEntity = findCustomerByUserUuid(userUuid);
        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);
        orderItemValidator.validateCustomerOwnershipOfOrderItem(customerJpaEntity, orderItem);

        return orderItemMapper.toResponseDto(orderItem);
    }

    @Override
    public OrderItemCountResponseDto orderItemDashboardCount(UUID userUuid) {
        Customer custoemr = findCustomerByUserUuid(userUuid);

        int orderItemPendingCount = orderItemPersistencePort.findOrderItemCountByStatus(custoemr.getCustomerUuid(), OrderItemStatus.PENDING);
        int orderItemPendingApprovalCount = orderItemPersistencePort.findOrderItemCountByStatus(custoemr.getCustomerUuid(), OrderItemStatus.PENDING_APPROVAL);
        int orderItemAllCount = orderItemPersistencePort.findOrderItemCount(custoemr.getCustomerUuid());

        return orderItemMapper.orderItemDashboardCount(orderItemAllCount, orderItemPendingCount, orderItemPendingApprovalCount);
    }

    /**
     * 유저가 견적서 작성으로 넘어가기 전에 url을 수정 가능하게 하는 프로세스
     *
     * @param userUuid
     * @param orderItemUuid
     * @param updateDto
     * @return
     */
    @Override
    @Transactional
    public OrderItem updateOrderItemWithValidate(UUID userUuid, UUID orderItemUuid, OrderItemUpdateDto updateDto) {
        Customer customer = findCustomerByUserUuid(userUuid);
        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);
        orderItemValidator.validateCustomerOwnershipOfOrderItem(customer, orderItem);

        orderValidator.requireDraftOrderStatus(orderItem.getOrder());

        orderItem.updateOrderItem(updateDto);

        return orderItem;
    }

    /**
     * 관리자가 주문 수정 - 유저 전체의 상품 update 가능
     *
     * @param userUuid
     * @param orderItemUuid
     * @param updateDto
     * @return
     */
    @Override
    public OrderItem updateOrderItemByAdmin(UUID userUuid, UUID orderItemUuid, OrderItemUpdateDto updateDto) {

        User user = userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("User not found for user uuid: %s")));
        user.validateAdminRole();

        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);

        orderItem.updateOrderItem(updateDto);

        return orderItem;
    }

    @Override
    public Optional<OrderItem> approveOrderItemByAdmin() {
        return Optional.empty();
    }

    @Override
    @Transactional
    public List<OrderItem> firstOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID productPaymentUuid) {
        List<OrderItem> orderItems = orderItemValidator.validateOrderItems(customerUuid, request.getOrderItemUuids(), OrderItemStatus.PENDING_APPROVAL);

        orderItems.forEach(orderItem -> orderItem.markAsPaymentCompleted(productPaymentUuid));

        List<OrderItem> savedOrderItems = orderItemPersistencePort.saveAllOrderItem(orderItems);
        return savedOrderItems;
    }

    //helper

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", userUuid)));
    }


    public OrderItem findOrderItemByOrderItemUuid(UUID orderItemUuid) {
        return orderItemPersistencePort.findByOrderItemUuid(orderItemUuid)
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("OrderItem not found for orderItemUuid: %s", orderItemUuid)));
    }
}
