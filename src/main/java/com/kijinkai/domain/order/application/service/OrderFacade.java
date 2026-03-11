package com.kijinkai.domain.order.application.service;


import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.order.application.dto.OrderPaymentRequestDto;
import com.kijinkai.domain.order.application.dto.OrderRequestDto;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;
import com.kijinkai.domain.order.application.mapper.OrderMapper;
import com.kijinkai.domain.order.application.port.in.CreateOrderUseCase;
import com.kijinkai.domain.order.application.port.in.OrderFacadeUseCase;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.port.in.GetOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.port.in.orderPayment.UpdateOrderPaymentUseCase;
import com.kijinkai.domain.payment.application.service.facade.OrderPaymentFacade;
import com.kijinkai.domain.payment.domain.exception.PaymentProcessingException;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.util.BusinessCodeType;
import com.kijinkai.util.GenerateBusinessItemCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class OrderFacade implements OrderFacadeUseCase {

    private final CreateOrderUseCase createOrderUseCase;

    private final OrderPaymentFacade orderPaymentFacade;
    private final UpdateOrderPaymentUseCase updateOrderPaymentUseCase;

    private final CustomerPersistencePort customerPersistencePort;
    private final GetOrderItemUseCase getOrderItemUseCase;
    private final GenerateBusinessItemCode generateBusinessItemCode;
    private final UserPersistencePort userPersistencePort;
    private final UpdateOrderItemUseCase updateOrderItemUseCase;
    private final ExchangeRateService exchangeRateService;


    private final OrderMapper orderMapper;

    private static final BigDecimal INSPECTION_FEE = BigDecimal.valueOf(300);

    /**
     * 유저가 견적을 검토한 후 결제를 진행하는 프로세스
     * --- 결제 서비스로 이동
     *
     * @param userUuid
     * @param orderUuid
     * @return 결제 완료된 주문 응답 DTO
     */
    @Override
    public OrderResponseDto completedOrder(UUID userUuid, OrderPaymentRequestDto requestDto) {

        // 조회 및 검증
        User user = userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        user.validateActive();

        Customer customer = customerPersistencePort.findByUserUuid(user.getUserUuid())
                .orElseThrow(() -> new CustomerNotFoundException("구매자를 찾을 수 없습니다."));

        // 환률 조회
        ExchangeRateResponseDto exchangeRateByKor = exchangeRateService.getExchangeRateInfoByCurrency(Currency.KRW);
        BigDecimal rate = exchangeRateByKor.getRate();

        // 주문 코드 생성
        String orderCode = generateBusinessItemCode.generateBusinessCode(userUuid.toString(), BusinessCodeType.ORD);


        // 검수 대상 아이템 추출
        List<UUID> inspectionItemUuids = requestDto.getOrderItemRequests().stream()
                .filter(OrderPaymentRequestDto.OrderItemRequest::isInspectedPhotoRequest)
                .map(OrderPaymentRequestDto.OrderItemRequest::getOrderItemUuid)
                .distinct()
                .toList();

        // 검수비 계산
        BigDecimal totalPhotoImageFee = INSPECTION_FEE.add(BigDecimal.valueOf(inspectionItemUuids.size()));

        // 전체 주문상품 조회
        List<OrderItem> orderItems = getOrderItemUseCase.getOrderItemByOrderItemUuids(
                requestDto.getOrderItemRequests().stream()
                        .map(OrderPaymentRequestDto.OrderItemRequest::getOrderItemUuid)
                        .toList()
        );


        // 결제 프로세스 진행
        OrderPayment orderPayment = orderPaymentFacade.processProductPayment(
                customer,
                orderItems,
                requestDto.getUserCouponUuid(),
                totalPhotoImageFee,
                rate
        );

        Map<UUID, BigDecimal> discountMap = calculateDiscountMap(orderItems, orderPayment.getDiscountAmount());

        updateOrderItemUseCase.processFirstPaymentAndRequestPhotos(inspectionItemUuids, orderItems, rate, discountMap);


        try {
            return executeWithOptimisticLockRetry(() -> {

                // 저장
                OrderResponseDto savedOrder = createOrderUseCase.createAndSaveOrder(
                        customer.getCustomerUuid(),
                        orderItems,
                        inspectionItemUuids,
                        orderCode,
                        orderPayment.getFinalPaymentAmount()
                );

                return orderMapper.toOrderResponse(savedOrder, orderPayment);

            });

        } catch (Exception e) {
            log.error("주문 저장 최종 실패. 결제 취소 진행합니다. error={}", e.getMessage());
            // 취소 처리 및 환불
            updateOrderPaymentUseCase.failOrderPayment(customer.getCustomerUuid(), orderPayment.getPaymentUuid());

            throw e;
        }
    }


    /**
     * 결제 재시도 (동시성 해결)
     *
     * @param operation
     * @return
     */
    private OrderResponseDto executeWithOptimisticLockRetry(Supplier<OrderResponseDto> operation) {
        int maxRetries = 3;
        for (int retryCount = 1; retryCount <= maxRetries; retryCount++) {
            try {
                return operation.get();
            } catch (OptimisticLockingFailureException e) {
                if (retryCount == maxRetries) {
                    log.error("Fail retry failed for optimistic lock");
                    throw new PaymentProcessingException("동시 접속자가 많아 결제에 실패했습니다.");
                }

                long waitTime = (long) (Math.pow(2, retryCount) * 100);
                waitForRetry(waitTime);
                log.warn("Retry {}/{} due to conflict", retryCount, maxRetries);
            }
        }

        throw new PaymentProcessingException("Unexpected error in payment completion");
    }

    private void waitForRetry(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new PaymentProcessingException("결제 처리 중 인터럽스가 발생하였습니다.");
        }
    }

    private Map<UUID, BigDecimal> calculateDiscountMap(List<OrderItem> orderItems, BigDecimal totalCouponDiscount) {
        Map<UUID, BigDecimal> discountMap = new HashMap<>();
        BigDecimal processedDiscountSum = BigDecimal.ZERO;

        // 1. 전체 상품 원가 합계
        BigDecimal totalOrderItemPrice = orderItems.stream()
                .map(OrderItem::getPriceOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 0 이하로 나누기 방지
        if (totalOrderItemPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyMap();
        }

        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem orderItem = orderItems.get(i);
            BigDecimal itemDiscount;

            if (i < orderItems.size() - 1) {
                itemDiscount = totalCouponDiscount
                        .multiply(orderItem.getPriceOriginal())
                        .divide(totalOrderItemPrice, 0, RoundingMode.HALF_UP);

                processedDiscountSum = processedDiscountSum.add(itemDiscount);
            } else {
                itemDiscount = totalCouponDiscount.subtract(processedDiscountSum);
            }

            discountMap.put(orderItem.getOrderItemUuid(), itemDiscount);
        }
        return discountMap;
    }
}
