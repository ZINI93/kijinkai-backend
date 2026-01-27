package com.kijinkai.domain.order.application.service;


import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.order.application.dto.OrderRequestDto;
import com.kijinkai.domain.order.application.dto.OrderResponseDto;
import com.kijinkai.domain.order.application.mapper.OrderMapper;
import com.kijinkai.domain.order.application.port.in.CreateOrderUseCase;
import com.kijinkai.domain.order.application.port.in.OrderFacadeUseCase;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.port.in.GetOrderItemUseCase;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.port.in.orderPayment.UpdateOrderPaymentUseCase;
import com.kijinkai.domain.payment.application.service.facade.OrderPaymentFacade;
import com.kijinkai.domain.payment.domain.exception.PaymentProcessingException;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import com.kijinkai.util.BusinessCodeType;
import com.kijinkai.util.GenerateBusinessItemCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;


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

    private final OrderMapper orderMapper;



    /**
     * 유저가 견적을 검토한 후 결제를 진행하는 프로세스
     * --- 결제 서비스로 이동
     *
     * @param userUuid
     * @param orderUuid
     * @return 결제 완료된 주문 응답 DTO
     */
    @Override
    public OrderResponseDto completedOrder(UUID userUuid, OrderRequestDto requestDto) {

        // userUuid -> 구매자 정보 조회
        Customer customer = customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException("Not found customer"));

        // 주문 코드 생성
        String orderCode = generateBusinessItemCode.generateBusinessCode(userUuid.toString(), BusinessCodeType.ORD);

        // 결제 금액 계산용
        List<OrderItem> orderItems = getOrderItemUseCase.getOrderItemsByCodeAndStatus(requestDto.getOrderItemCodes(), OrderItemStatus.PENDING_APPROVAL);

        // 주문 상품 결제
        OrderPayment orderPayment = orderPaymentFacade.processProductPayment(customer, orderItems);




        try {
            return executeWithOptimisticLockRetry(() -> {

                // 재시도할때 마다 최신 상품 리스트 갱신
                List<OrderItem> freshOrderItems = getOrderItemUseCase.getOrderItemsByCodeAndStatus(requestDto.getOrderItemCodes(), OrderItemStatus.PENDING_APPROVAL);

                // 저장
                return createOrderUseCase.createAndSaveOrder(
                        customer.getCustomerUuid(),
                        freshOrderItems,
                        requestDto.getInspectedPhotoRequest(),
                        orderCode,
                        orderPayment.getPaymentAmount()
                );

            });
        }catch (Exception e){
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
}
