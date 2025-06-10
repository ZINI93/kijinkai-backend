package com.kijinkai.domain.order.service;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.order.dto.OrderRequestDto;
import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.dto.OrderUpdateDto;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.entity.OrderStatus;
import com.kijinkai.domain.order.exception.OrderNotFoundException;
import com.kijinkai.domain.order.fectory.OrderFactory;
import com.kijinkai.domain.order.mapper.OrderMapper;
import com.kijinkai.domain.order.repository.OrderRepository;
import com.kijinkai.domain.order.validator.OrderValidator;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.mapper.OrderItemMapper;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import com.kijinkai.domain.orderitem.service.OrderItemService;
import com.kijinkai.domain.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderFactory orderfactory;
    private final UserValidator userValidator;
    private final OrderValidator orderValidator;

    private final OrderItemService orderItemService;


    @Override  // 유저가 링크, 가격을 입력
    public OrderResponseDto createOrderProcess(String userUuid, OrderRequestDto requestDto) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = orderfactory.createOrder(customer, requestDto.getMemo());
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItem = requestDto.getOrderItems().stream().map(item ->
                orderItemService.createOrderItemProcess(customer, order, item)).collect(Collectors.toList());

        orderItemRepository.saveAll(orderItem);

        return orderMapper.toResponse(savedOrder);
    }

    private void updateOrder(Order order, OrderUpdateDto updateDto) {
        order.updateOrder(updateDto.getOrderstate(), updateDto.getMemo());
    }

    @Override
    public OrderResponseDto updateOrderWithValidate(String userUuid, String orderUuid, OrderUpdateDto updateDto) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);

        orderValidator.requireAwaitingPaymentStatus(order);

        updateDto.getOrderItems().forEach(
                itemUpdate ->
                        orderItemService.updateOrderItemWithValidate(orderUuid, itemUpdate)
        );
        updateOrder(order, updateDto);


        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderInfo(String userUuid, String orderUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        return orderMapper.toResponse(order);
    }


    @Override
    @Transactional // 관리자가 결제 완료된 주문서를 보고 배송 준비 시작단계
    public OrderResponseDto confirmOrder(String userUuid, String orderUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        userValidator.requireAdminRole(customer.getUser());
        orderValidator.requirePaidStatusForConfirmation(order);
        order.updateOrderState(OrderStatus.PREPARE_DELIVERY);

        return orderMapper.toResponse(order);
    }

    @Override // 유저가 오더 캔슬
    public OrderResponseDto cancelOrder(String userUuid, String orderUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requireCancellableStatus(order);
        order.updateOrderState(OrderStatus.CANCEL);
        return orderMapper.toResponse(order);
    }

    @Override //관리자가 강제 삭제
    public void deleteOrder(String userUuid, String orderUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        userValidator.requireAdminRole(customer.getUser());

        orderRepository.delete(order);
    }

    private Customer findCustomerByUserUuid(String userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException("UserUuid: Customer not found"));
    }

    private Order findOrderByCustomerUuidAndOrderUuid(Customer customer, String orderUuid) {
        return orderRepository.findByCustomerCustomerUuidAndOrderUuid(customer.getCustomerUuid(), orderUuid)
                .orElseThrow(() -> new OrderNotFoundException("Customer UUid And OrderUuid : Order not found"));
    }
}
