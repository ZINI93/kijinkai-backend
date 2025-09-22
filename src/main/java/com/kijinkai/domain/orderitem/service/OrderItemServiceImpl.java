package com.kijinkai.domain.orderitem.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.validator.OrderValidator;
import com.kijinkai.domain.orderitem.dto.OrderItemCountResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.factory.OrderItemFactory;
import com.kijinkai.domain.orderitem.mapper.OrderItemMapper;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import com.kijinkai.domain.orderitem.validator.OrderItemValidator;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
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
public class OrderItemServiceImpl implements OrderItemService {

    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;

    private final PriceCalculationService priceCalculationService;

    private final OrderItemValidator orderItemValidator;
    private final OrderValidator orderValidator;
    private final UserApplicationValidator userValidator;
    private final OrderItemFactory orderItemFactory;
    private final OrderItemMapper orderItemMapper;

    private final ExchangeRateService exchangeRateService;



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
    public OrderItem updateOrderItemWithValidate(UUID userUuid, String orderItemUuid, OrderItemUpdateDto updateDto) {

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
    @Transactional
    public OrderItem updateOrderItemByAdmin(UUID userUuid, String orderItemUuid, OrderItemUpdateDto updateDto) {
        Customer customer = findCustomerByUserUuid(userUuid);
        userValidator.requireJpaAdminRole(customer.getUser());

        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);

        orderItem.updateOrderItem(updateDto);

        return orderItem;
    }

    private OrderItem findOrderItemByOrderItemUuid(String orderItemUuid) {
        return orderItemRepository.findByOrderItemUuid(UUID.fromString(orderItemUuid))
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("Order item not found for order item uuid: %s", orderItemUuid)));
    }


    /**
     * 상품 주문취소
     *
     * @param orderItemUuid
     */
    public void cancelOrderItem(String orderItemUuid) {
        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);
        orderItem.isCancel();
    }


    /**
     * 구매자의 구매한 상품 전체 내역 / 주문별로 정리
     *
     * @param userUuid
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderItemResponseDto> getOrderItems(UUID userUuid, Pageable pageable) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Page<OrderItem> orderItems = orderItemRepository.findAllByOrderCustomerCustomerUuidOrderByOrderCreatedAtDesc(customer.getCustomerUuid(), pageable);
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
        Page<OrderItem> orderItems = orderItemRepository.findAllByOrderCustomerCustomerUuidAndOrderItemStatusOrderByOrderCreatedAtDesc(customer.getCustomerUuid(), orderItemStatus, pageable);
        return orderItems.map(orderItemMapper::toResponseDto);
    }


    // orderitem uuid를 찾고, 상태 변경을 해준다.
    @Override @Transactional
    public List<OrderItem> firstOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID productPaymentUuid) {
        List<OrderItem> orderItems = orderItemValidator.validateOrderItems(customerUuid, request.getOrderItemUuids(), OrderItemStatus.PENDING_APPROVAL);

        orderItems.forEach(orderItem -> orderItem.markAsPaymentCompleted(productPaymentUuid));

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);
        return savedOrderItems;
    }

    // 일단 사용하고 리펙토링 할때 위에 코드랑 유사함으로 결합 고려해야함
    @Override @Transactional
    public List<OrderItem> secondOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID deliveryPaymentUuid) {
        List<OrderItem> orderItems = orderItemValidator.validateOrderItems(customerUuid, request.getOrderItemUuids(), OrderItemStatus.PRODUCT_PAYMENT_COMPLETED);
        orderItems.forEach(orderItem -> orderItem.markAsDeliveryPaymentRequest(deliveryPaymentUuid));
        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);
        return savedOrderItems;
    }

    @Override
    public OrderItemCountResponseDto orderItemDashboardCount(UUID userUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);

        int orderItemPendingCount = orderItemRepository.findOrderItemCountByStatus(customer.getCustomerUuid(), OrderItemStatus.PENDING);
        int orderItemPendingApprovalCount = orderItemRepository.findOrderItemCountByStatus(customer.getCustomerUuid(), OrderItemStatus.PENDING_APPROVAL);
        int orderItemAllCount = orderItemRepository.findOrderItemCount(customer.getCustomerUuid());

        return orderItemMapper.orderItemDashboardCount(orderItemAllCount, orderItemPendingCount, orderItemPendingApprovalCount);
    }


    /**
     * 구매자의 상품 조회
     *
     * @param userUuid
     * @param orderItemUuid
     * @return
     */
    @Override
    public OrderItemResponseDto getOrderItemInfo(UUID userUuid, String orderItemUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);
        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);
        orderItemValidator.validateCustomerOwnershipOfOrderItem(customer, orderItem);

        return orderItemMapper.toResponseDto(orderItem);
    }

    /**
     * 상품 취소
     * @param orderItemUuid
     */
    @Override
    @Transactional
    public void deleteOrderItem(UUID orderItemUuid) {

        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);
        orderItemValidator.requiredPendingStatus(orderItem);
        orderItemRepository.delete(orderItem);
    }

    // ---- 관리자 기능 ----

    @Override
    public Optional<OrderItem> approveOrderItemByAdmin() {
        return Optional.empty();
    }

    // helper method
    @Override
    public OrderItem findOrderItemByOrderItemUuid(UUID orderItemUuid) {
        return orderItemRepository.findByOrderItemUuid(orderItemUuid)
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("Order item not found for order item uuid: %s", orderItemUuid)));
    }


    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid: %s", userUuid)));
    }

}
