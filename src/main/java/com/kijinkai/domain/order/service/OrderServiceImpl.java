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
import com.kijinkai.domain.order.exception.OrderValidationException;
import com.kijinkai.domain.order.fectory.OrderFactory;
import com.kijinkai.domain.order.mapper.OrderMapper;
import com.kijinkai.domain.order.repository.OrderRepository;
import com.kijinkai.domain.order.validator.OrderValidator;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.mapper.OrderItemMapper;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import com.kijinkai.domain.orderitem.service.OrderItemService;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.repository.WalletRepository;
import com.kijinkai.domain.wallet.validator.WalletValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal EXCHANGE_RATE_TEMP = new BigDecimal("0.2");

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    private final OrderFactory orderFactory;

    private final WalletValidator walletValidator;
    private final UserValidator userValidator;
    private final OrderValidator orderValidator;

    private final OrderItemService orderItemService;

    // 유저는 간단하게 링크 작성을 한다, 가격작성도 허용
    // 그리고 관리자가 링크의 가격을 조사해서, 견적서를 준다
    // 유저는 검토 후 구입버튼을 누르고 본인 월렛에 있는 돈을 지불한다

    @Override  // 유저가 링크, 가격을 입력
    public OrderResponseDto createOrderProcess(String userUuid, OrderRequestDto requestDto) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = orderFactory.createOrder(customer, requestDto.getMemo());
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItem = requestDto.getOrderItems().stream().map(item ->
                orderItemService.createOrderItemProcess(customer, order, item)).collect(Collectors.toList());
        orderItemRepository.saveAll(orderItem);

        return orderMapper.toResponse(savedOrder);
    }

    @Override // 견적서제출 - 유저로 부터 받은 링크의 가격을 입력후 제출한다.
    public OrderResponseDto updateOrderEstimate(String userUuid, String orderUuid, OrderUpdateDto updateDto) {

        Customer customer = findCustomerByUserUuid(userUuid);
        userValidator.requireAdminRole(customer.getUser());

        Order order = findOrderByOrderUuid(orderUuid);
        orderValidator.requireDraftOrderStatus(order);

        BigDecimal calculateTotalAmount = BigDecimal.ZERO;

        for (OrderItemUpdateDto itemUpdate : updateDto.getOrderItems()) {
            OrderItem orderItem = orderItemRepository.findByOrderItemUuid(itemUpdate.getOrderItemUuid())
                    .orElseThrow(() -> new OrderItemNotFoundException("Order item with UUID: " + itemUpdate.getOrderItemUuid() + " not found."));

            orderItem.validateOrderAndOrderItem(order);
            orderItem.updateEstimatedPrice();

            orderItem.updateEstimatedPrice(itemUpdate.getPriceOriginal());
            orderItemRepository.save(orderItem);

            calculateTotalAmount = calculateTotalAmount.add(orderItem.getPriceOriginal());
        }

        BigDecimal calculateTotalAmountConverter = calculateTotalAmount.multiply(EXCHANGE_RATE_TEMP); // 임시 환률

        updateOrderEstimate(order, calculateTotalAmount, calculateTotalAmountConverter, updateDto);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }


    @Override // 유저가 검토 후 돈을 지불
    public OrderResponseDto completedOrder(String userUuid, String orderUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requireAwaitingOrderStatus(order);

        Wallet wallet = findWalletByCustomerUuid(customer);
        walletValidator.requireSufficientBalance(wallet);
        walletValidator.requireActiveStatus(wallet);

        wallet.decreaseBalance(order.getTotalPriceOriginal());
        walletRepository.save(wallet);

        order.completePayment();  // order status change paid
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
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
        userValidator.requireAdminRole(customer.getUser());

        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requirePaidStatusForConfirmation(order);

        order.updateOrderState(OrderStatus.PREPARE_DELIVERY);

        return orderMapper.toResponse(order);
    }

    @Override // 유저가 오더 캔슬
    public OrderResponseDto cancelOrder(String userUuid, String orderUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requireCancellableStatus(order);

        order.cancelOrder();
        ;
        return orderMapper.toResponse(order);
    }

    @Override //관리자가 강제 삭제
    public void deleteOrder(String userUuid, String orderUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        userValidator.requireAdminRole(customer.getUser());

        orderRepository.delete(order);
    }

    private void updateOrderEstimate(Order order, BigDecimal totalAmountOriginal, BigDecimal finalAmount, OrderUpdateDto orderUpdateDto) {
        order.updateOrderEstimate(totalAmountOriginal, finalAmount, orderUpdateDto.getConvertedCurrency(), orderUpdateDto.getMemo());
    }

    private Wallet findWalletByCustomerUuid(Customer customer) {
        return walletRepository.findByCustomerCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new WalletNotFoundException("Customer Id : Wallet not found"));
    }

    private Order findOrderByOrderUuid(String orderUuid) {
        return orderRepository.findByOrderUuid(orderUuid)
                .orElseThrow(() -> new OrderNotFoundException("Order uuid: Order not found"));
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
