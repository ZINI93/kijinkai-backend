package com.kijinkai.domain.order.application.service;


import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.order.adapter.out.persistence.entity.OrderStatus;
import com.kijinkai.domain.order.application.dto.OrderRequestDto;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;
import com.kijinkai.domain.order.application.dto.OrderUpdateDto;
import com.kijinkai.domain.order.application.mapper.OrderMapper;
import com.kijinkai.domain.order.application.port.in.CreateOrderUseCase;
import com.kijinkai.domain.order.application.port.in.DeleteOrderUseCase;
import com.kijinkai.domain.order.application.port.in.GetOrderUseCase;
import com.kijinkai.domain.order.application.port.in.UpdateOrderUseCase;
import com.kijinkai.domain.order.application.port.out.OrderPersistencePort;
import com.kijinkai.domain.order.application.validator.OrderValidator;
import com.kijinkai.domain.order.domain.exception.OrderCreationException;
import com.kijinkai.domain.order.domain.exception.OrderNotFoundException;
import com.kijinkai.domain.order.domain.factory.OrderFactory;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.application.dto.OrderItemRequestDto;
import com.kijinkai.domain.orderitem.application.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.application.service.OrderItemApplicationService;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.domain.exception.OrderUpdateException;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.domain.exception.PaymentProcessingException;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.transaction.service.TransactionService;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.domain.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.domain.exception.WalletUpdateFailedException;
import com.kijinkai.domain.wallet.adapter.out.persistence.repository.WalletRepository;
import com.kijinkai.domain.wallet.application.validator.WalletValidator;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase, UpdateOrderUseCase, DeleteOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final OrderMapper orderMapper;
    private final OrderFactory orderFactory;

    private final OrderValidator orderValidator;


    //outer
    private final OrderItemApplicationService orderItemApplicationService;
    private final PriceCalculationService priceCalculationService;
    private final TransactionService transactionService;
    private final WalletValidator walletValidator;
    private final UserApplicationValidator userApplicationValidator;
    private final OrderItemPersistencePort orderItemPersistencePort;
    private final CustomerPersistencePort customerPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final WalletPersistencePort walletPersistencePort;


    /**
     * 사용자가 링크와 가격을 입력하여 주문을 생성하는 프로세스
     *
     * @param userUuid   사용자UUID
     * @param requestDto 주문 요청 DTO
     * @return 생성된 주문 응답 DTO
     */
    @Override
    @Transactional
    public OrderResponseDto createOrder(UUID userUuid, OrderRequestDto requestDto) {
        log.info("Crating order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);

        try {
            BigDecimal totalPrice = requestDto.getOrderItems().stream()
                    .map(OrderItemRequestDto::getPriceOriginal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Order order = orderFactory.createOrder(customer.getCustomerUuid(), requestDto.getMemo(), totalPrice);
            Order savedOrder = orderPersistencePort.saveOrder(order);

            List<OrderItem> orderItems = requestDto.getOrderItems().stream()
                    .map(item -> orderItemApplicationService.createOrderItem(customer, savedOrder, item))
                    .collect(Collectors.toList());
            orderItemPersistencePort.saveAllOrderItem(orderItems);

            log.info("Crated order for order uuid:{}", savedOrder.getOrderUuid());

            return orderMapper.toResponse(savedOrder);
        } catch (Exception e) {
            log.error("Failed to create order for user uuid: {}", userUuid, e);
            throw new OrderCreationException("Failed to create order", e);
        }
    }

    /**
     * 관리자가 취소된 주문을 강제로 삭제합니다.
     *
     * @param userUuid
     * @param orderUuid
     */
    @Override
    @Transactional
    public void deleteOrder(UUID userUuid, UUID orderUuid) {
        log.info("Deleting order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);

        User user = findUserByUserUuid(userUuid);
        userApplicationValidator.requireAdminRole(user);

        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requireCancellableStatus(order);

        orderPersistencePort.deleteOrder(order);
    }

    /**
     * 특정 주문의 정보를 조회합니다.
     *
     * @param userUuid
     * @param orderUuid
     * @return 주문 응답 DTO
     */
    @Override
    public OrderResponseDto getOrderInfo(UUID userUuid, UUID orderUuid) {

        log.info("Searching order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);

        return orderMapper.toResponse(order);
    }

    /**
     * 유저가 견적을 검토한 후 결제를 진행하는 프로세스
     * --- 결제 서비스로 이동
     *
     * @param userUuid
     * @param orderUuid
     * @return 결제 완료된 주문 응답 DTO
     */
    @Override
    @Transactional
    public OrderResponseDto completedOrder(UUID userUuid, UUID orderUuid) {
        return executeWithOptimisticLockRetry(() ->
                processOrderCompletion(userUuid, orderUuid));
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderEstimate(UUID userUuid, UUID orderUuid, OrderUpdateDto updateDto) {
        log.info("updating order for user uuid: {}, order uuid: {}", userUuid, orderUuid);

        try {

            User user = findUserByUserUuid(userUuid);
            userApplicationValidator.requireAdminRole(user);

            Order order = findOrderByOrderUuid(orderUuid);
            orderValidator.requireDraftOrderStatus(order);


            BigDecimal calculateTotalAmountOriginal = BigDecimal.ZERO; // 원본 통화(USD 등)의 총액

            for (OrderItemUpdateDto itemUpdate : updateDto.getOrderItems()) {
                OrderItem orderItem = orderItemPersistencePort.findByOrderUuid(orderUuid)
                        .orElseThrow(() -> new OrderItemNotFoundException("OrderUUID: " + order.getOrderUuid() + " not found."));

                orderItem.validateOrderAndOrderItem(order);
                orderItem.updateEstimatedPrice();

                orderItemPersistencePort.saveOrderItem(orderItem);

                calculateTotalAmountOriginal = calculateTotalAmountOriginal.add(orderItem.getPriceOriginal());
            }

            BigDecimal convertedTotalAmount = priceCalculationService.calculateTotalPrice(calculateTotalAmountOriginal);

            updateOrderEstimate(user.getUserUuid(), order.getOrderUuid(), updateDto);
            Order savedOrder = orderPersistencePort.saveOrder(order);

            log.info("Updated order for order uuid:{}", savedOrder.getOrderUuid());

            return orderMapper.toResponse(savedOrder);
        } catch (Exception e) {
            log.error("Failed to update order estimate for order uuid: {}", orderUuid, e);
            throw new OrderUpdateException("Failed to update order estimate", e);
        }
    }

    @Override
    @Transactional
    public OrderResponseDto cancelOrder(UUID userUuid, UUID orderUuid) {
        log.info("Canceling order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requireCancellableStatus(order);

        log.info("Canceled order for order uuid:{}", order.getOrderUuid());

        order.cancelOrder();

        return orderMapper.toResponse(order);
    }

    /**
     * 관리자가 결제 완료된 주문서를 보고 배송 준비 단계로 전환합니다.
     *
     * @param userUuid
     * @param orderUuid
     * @return 배송 준비 상태의 주문 응답 DTO
     */
    @Override
    @Transactional
    public OrderResponseDto confirmOrder(UUID userUuid, UUID orderUuid) {
        log.info("Confirming order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);

        User user = findUserByUserUuid(userUuid);
        userApplicationValidator.requireAdminRole(user);

        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requirePaidStatusForConfirmation(order);

        order.updateOrderStatus(OrderStatus.PREPARE_DELIVERY);

        log.info("Confirmed order for order uuid:{}", order.getOrderUuid());

        return orderMapper.toResponse(order);
    }


    // helper method
    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found userUuid: %s", userUuid)));
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid %s", userUuid)));
    }

    private Order findOrderByCustomerUuidAndOrderUuid(Customer customer, UUID orderUuid) {
        return orderPersistencePort.findByCustomerUuidAndOrderUuid(customer.getCustomerUuid(), orderUuid)
                .orElseThrow(() -> new OrderNotFoundException(String.format("OrderJpaEntity not found for customer uuid: %s and order uuid: %s", customer.getCustomerUuid(), orderUuid)));
    }


    private Wallet findWalletByCustomerUuid(UUID customerUuid) {
        return walletPersistencePort.findByCustomerUuid(customerUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("WalletJpaEntity not found for customer uuid: %s", customerUuid)));
    }

    private Order findOrderByOrderUuid(UUID orderUuid) {
        return orderPersistencePort.findByOrderUuid(orderUuid)
                .orElseThrow(() -> new OrderNotFoundException(String.format("OrderJpaEntity not found for order uuid: %s", orderUuid)));
    }

    // -----

    public OrderResponseDto executeWithOptimisticLockRetry(Supplier<OrderResponseDto> operation) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                return operation.get();
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                log.warn("Optimistic lock failure, retry{}/{}", retryCount, maxRetries);

                if (retryCount >= maxRetries) {
                    throw new PaymentProcessingException("Payment completion failed due to concurrent access");
                }

                try {
                    Thread.sleep(100 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new PaymentProcessingException("Payment processing interrupted");
                }
            }
        }
        throw new PaymentProcessingException("Unexpected error in payment completion");
    }

    private OrderResponseDto processOrderCompletion(UUID userUuid, UUID orderUuid) {

        log.info("Completing order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requireAwaitingOrderStatus(order);

        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());
        walletValidator.requireActiveStatus(wallet);

        BigDecimal amountToPay = order.getTotalPriceOriginal();
        walletValidator.requireSufficientBalance(wallet, amountToPay);

        int updatedRows = walletPersistencePort.decreaseBalanceAtomic(wallet.getWalletUuid(), amountToPay);

        if (updatedRows == 0) {
            throw new WalletUpdateFailedException("Insufficient balance for payment or wallet update failed");
        }

        order.completePayment();  // order status change paid
        Order savedOrder = orderPersistencePort.saveOrder(order);


        Wallet updateWallet = walletPersistencePort.findByWalletId(wallet.getWalletId()).orElseThrow(() -> new WalletNotFoundException(String.format("WalletJpaEntity not found for wallet id: %s", wallet.getWalletId())));
        BigDecimal balanceAfter = updateWallet.getBalance();
        transactionService.createTransactionWithValidate(userUuid, wallet.getWalletUuid(), orderUuid, TransactionType.PAYMENT, amountToPay, wallet.getBalance(), balanceAfter, TransactionStatus.COMPLETED);

        log.info("Completed order for order uuid:{}", savedOrder.getOrderUuid());

        return orderMapper.toResponse(savedOrder);
    }
}

