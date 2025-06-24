package com.kijinkai.domain.order.service;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.exchange.calculator.CurrencyExchangeCalculator;
import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.order.dto.OrderRequestDto;
import com.kijinkai.domain.order.dto.OrderResponseDto;
import com.kijinkai.domain.order.dto.OrderUpdateDto;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.entity.OrderStatus;
import com.kijinkai.domain.order.exception.OrderCreationException;
import com.kijinkai.domain.order.exception.OrderNotFoundException;
import com.kijinkai.domain.order.factory.OrderFactory;
import com.kijinkai.domain.order.mapper.OrderMapper;
import com.kijinkai.domain.order.repository.OrderRepository;
import com.kijinkai.domain.order.validator.OrderValidator;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.entity.Currency;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.exception.OrderUpdateException;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import com.kijinkai.domain.orderitem.service.OrderItemService;
import com.kijinkai.domain.payment.exception.PaymentProcessingException;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.transaction.service.TransactionService;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.exception.WalletUpdateFailedException;
import com.kijinkai.domain.wallet.repository.WalletRepository;
import com.kijinkai.domain.wallet.validator.WalletValidator;
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
@Transactional
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final CurrencyExchangeCalculator exchangeCalculator;

    private final OrderMapper orderMapper;

    private final OrderFactory orderFactory;

    private final WalletValidator walletValidator;
    private final UserValidator userValidator;
    private final OrderValidator orderValidator;

    private final OrderItemService orderItemService;
    private final ExchangeRateService exchangeRateService;
    private final TransactionService transactionService;

    /**
     * 사용자가 링크와 가격을 입력하여 주문을 생성하는 프로세스
     *
     * @param userUuid   사용자UUID
     * @param requestDto 주문 요청 DTO
     * @return 생성된 주문 응답 DTO
     */
    @Override  // 유저가 링크, 가격을 입력
    public OrderResponseDto createOrderProcess(String userUuid, OrderRequestDto requestDto) {
        log.info("Crating order for user uuid:{}", userUuid);

        try {

            Customer customer = findCustomerByUserUuid(userUuid);
            Order order = orderFactory.createOrder(customer, requestDto.getMemo());
            Order savedOrder = orderRepository.save(order);

            List<OrderItem> orderItems = requestDto.getOrderItems().stream()
                    .map(item -> orderItemService.createOrderItem(customer, savedOrder, item))
                    .collect(Collectors.toList());

            orderItemRepository.saveAll(orderItems);

            log.info("Crated order for order uuid:{}", savedOrder.getOrderUuid());

            return orderMapper.toResponse(savedOrder);
        } catch (Exception e) {
            log.error("Failed to create order for user uuid: {}", userUuid, e);
            throw new OrderCreationException("Failed to create order", e);
        }
    }

    /**
     * 관리자가 사용자로부터 받은 링크의 가격을 입력하고 견적을 제출하는 프로세스
     * 예측이 불가능 하여 배송료는 포함되지 않으며, 최종 결제시 따로 부과됨
     *
     * @param userUuid
     * @param orderUuid
     * @param updateDto
     * @return 업데이트된 주문 응답 dto
     */
    @Override
    public OrderResponseDto updateOrderEstimate(String userUuid, String orderUuid, OrderUpdateDto updateDto) {
        log.info("updating order for user uuid: {}, order uuid: {}", userUuid, orderUuid);

        try {

            Customer customer = findCustomerByUserUuid(userUuid);
            userValidator.requireAdminRole(customer.getUser());

            Order order = findOrderByOrderUuid(orderUuid);
            orderValidator.requireDraftOrderStatus(order);

            BigDecimal calculateTotalAmountOriginal = BigDecimal.ZERO; // 원본 통화(USD 등)의 총액

            for (OrderItemUpdateDto itemUpdate : updateDto.getOrderItems()) {
                OrderItem orderItem = orderItemRepository.findByOrderItemUuid(UUID.fromString(itemUpdate.getOrderItemUuid()))
                        .orElseThrow(() -> new OrderItemNotFoundException("Order item with UUID: " + itemUpdate.getOrderItemUuid() + " not found."));

                orderItem.validateOrderAndOrderItem(order);
                orderItem.updateEstimatedPrice();

                orderItemRepository.save(orderItem);

                calculateTotalAmountOriginal = calculateTotalAmountOriginal.add(orderItem.getPriceOriginal());
            }

            // 1. 최신 환율 정보 가져오기
            BigDecimal exchangeRate = getLatestExchangeRate(Currency.JPY, updateDto.getConvertedCurrency());

            // 2. 환율을 적용하여 원화 총액 계산 //유저에게 원화로 얼마인지 정보 제공
            BigDecimal convertedTotalAmount = exchangeCalculator.calculateConvertedAmount(calculateTotalAmountOriginal, exchangeRate);
            exchangeCalculator.validateConverterAmount(calculateTotalAmountOriginal, exchangeRate);

            updateOrderEstimate(order, calculateTotalAmountOriginal, convertedTotalAmount, updateDto);
            Order savedOrder = orderRepository.save(order);

            log.info("Updated order for order uuid:{}", savedOrder.getOrderUuid());

            return orderMapper.toResponse(savedOrder);
        } catch (Exception e) {
            log.error("Failed to update order estimate for order uuid: {]", orderUuid, e);
            throw new OrderUpdateException("Failed to update order estimate", e);
        }
    }

    /**
     * 유저가 견적을 검토한 후 결제를 진행하는 프로세스
     *
     * @param userUuid
     * @param orderUuid
     * @return 결제 완료된 주문 응답 DTO
     */
    @Override
    public OrderResponseDto completedOrder(String userUuid, String orderUuid) {
        return executeWithOptimisticLockRetry(() ->
                processOrderCompletion(userUuid, orderUuid));
    }

    private OrderResponseDto processOrderCompletion(String userUuid, String orderUuid) {

        log.info("Completing order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requireAwaitingOrderStatus(order);

        Wallet wallet = findWalletByCustomerId(customer);
        walletValidator.requireActiveStatus(wallet);


        BigDecimal amountToPay = order.getTotalPriceOriginal();
        walletValidator.requireSufficientBalance(wallet, amountToPay);

        int updatedRows = walletRepository.decreaseBalanceAtomic(wallet.getWalletId(), amountToPay);

        if (updatedRows == 0) {
            throw new WalletUpdateFailedException("Insufficient balance for payment or wallet update failed");
        }

        order.completePayment();  // order status change paid
        Order savedOrder = orderRepository.save(order);


        Wallet updateWallet = walletRepository.findByWalletId(wallet.getWalletId()).orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found for wallet id: %s", wallet.getWalletId())));
        BigDecimal balanceAfter = updateWallet.getBalance();
        transactionService.createTransactionWithValidate(userUuid, wallet, order, TransactionType.PAYMENT, amountToPay, wallet.getBalance(), balanceAfter, TransactionStatus.COMPLETED);

        log.info("Completed order for order uuid:{}", savedOrder.getOrderUuid());

        return orderMapper.toResponse(savedOrder);
    }

    /**
     * 관리자가 결제 완료된 주문서를 보고 배송 준비 단계로 전환합니다.
     *
     * @param userUuid
     * @param orderUuid
     * @return 배송 준비 상태의 주문 응답 DTO
     */
    @Override // 관리자가 결제 완료된 주문서를 보고 배송 준비 시작단계
    public OrderResponseDto confirmOrder(String userUuid, String orderUuid) {

        log.info("Confirming order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        userValidator.requireAdminRole(customer.getUser());

        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requirePaidStatusForConfirmation(order);

        order.updateOrderState(OrderStatus.PREPARE_DELIVERY);

        log.info("Confirmed order for order uuid:{}", order.getOrderUuid());

        return orderMapper.toResponse(order);
    }

    /**
     * 특정 주문의 정보를 조회합니다.
     *
     * @param userUuid
     * @param orderUuid
     * @return 주문 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderInfo(String userUuid, String orderUuid) {

        log.info("Searching order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);

        return orderMapper.toResponse(order);
    }

    @Override // 유저가 오더 캔슬
    public OrderResponseDto cancelOrder(String userUuid, String orderUuid) {

        log.info("Canceling order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requireCancellableStatus(order);

        log.info("Canceled order for order uuid:{}", order.getOrderUuid());

        order.cancelOrder();

        return orderMapper.toResponse(order);
    }

    /**
     * 관리자가 취소된 주문을 강제로 삭제합니다.
     *
     * @param userUuid
     * @param orderUuid
     */
    @Override //관리자가 강제 삭제
    public void deleteOrder(String userUuid, String orderUuid) {

        log.info("Deleting order for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        userValidator.requireAdminRole(customer.getUser());

        Order order = findOrderByCustomerUuidAndOrderUuid(customer, orderUuid);
        orderValidator.requireCancellableStatus(order);

        orderRepository.delete(order);
    }

    private void updateOrderEstimate(Order order, BigDecimal totalAmountOriginal, BigDecimal finalAmount, OrderUpdateDto orderUpdateDto) {
        order.updateOrderEstimate(totalAmountOriginal, finalAmount, orderUpdateDto.getConvertedCurrency(), orderUpdateDto.getMemo());
    }

    private Wallet findWalletByCustomerId(Customer customer) {
        return walletRepository.findByCustomerCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found for customer uuid: %s", customer.getCustomerUuid())));
    }

    private Order findOrderByOrderUuid(String orderUuid) {
        return orderRepository.findByOrderUuid(UUID.fromString(orderUuid))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order not found for order uuid: %s", orderUuid)));
    }

    private Customer findCustomerByUserUuid(String userUuid) {
        return customerRepository.findByUserUserUuid(UUID.fromString(userUuid))
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid %s", userUuid)));
    }

    private Order findOrderByCustomerUuidAndOrderUuid(Customer customer, String orderUuid) {
        return orderRepository.findByCustomerCustomerUuidAndOrderUuid(UUID.fromString(customer.getCustomerUuid()), UUID.fromString(orderUuid))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order not found for customer uuid: %s and order uuid: %s", customer.getCustomerUuid(), orderUuid)));
    }

    /**
     * ExchangeRateService를 통해 최신 환율을 조회합니다.
     * 환율이 없는 경우 (예: 아직 API 호출 전 또는 실패)에 대한 견고한 처리 필요.
     *
     * @param fromCurrency 기준 통화 (예: USD)
     * @param toCurrency   대상 통화 (예: KRW)
     * @return 최신 환율 (BigDecimal)
     * @throws RuntimeException 환율 정보를 찾을 수 없을 경우
     */
    private BigDecimal getLatestExchangeRate(Currency fromCurrency, Currency toCurrency) {

        return exchangeRateService.getLatestExchangeRate(fromCurrency, toCurrency)
                .map(ExchangeRate::getRate) // ExchangeRate 엔티티에서 rate 값 추출
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Latest exchange rate for %s to %s not found. Please ensure exchange rate update is working.",
                                fromCurrency, toCurrency)));
    }

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
}
