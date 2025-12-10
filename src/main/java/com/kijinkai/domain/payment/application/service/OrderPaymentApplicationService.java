package com.kijinkai.domain.payment.application.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentDeliveryRequestDto;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentCountResponseDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;
import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
import com.kijinkai.domain.payment.application.port.in.orderPayment.CreateOrderPaymentUseCase;
import com.kijinkai.domain.payment.application.port.in.orderPayment.DeleteOrderPaymentUseCase;
import com.kijinkai.domain.payment.application.port.in.orderPayment.GetOrderPaymentUseCase;
import com.kijinkai.domain.payment.application.port.in.orderPayment.UpdateOrderPaymentUseCase;
import com.kijinkai.domain.payment.application.port.out.OrderPaymentPersistencePort;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import com.kijinkai.domain.payment.domain.exception.OrderPaymentCompletionException;
import com.kijinkai.domain.payment.domain.exception.OrderPaymentNotFoundException;
import com.kijinkai.domain.payment.domain.exception.OrderPaymentStatusException;
import com.kijinkai.domain.payment.domain.exception.PaymentProcessingException;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.application.port.in.UpdateWalletUseCase;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.domain.exception.InsufficientBalanceException;
import com.kijinkai.domain.wallet.domain.exception.WalletNotActiveException;
import com.kijinkai.domain.wallet.domain.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderPaymentApplicationService implements CreateOrderPaymentUseCase, GetOrderPaymentUseCase, UpdateOrderPaymentUseCase, DeleteOrderPaymentUseCase {

    private final CustomerPersistencePort customerPersistencePort;
    private final WalletPersistencePort walletPersistencePort;
    private final OrderItemPersistencePort orderItemPersistencePort;
    private final UserPersistencePort userPersistencePort;

    private final UpdateWalletUseCase updateWalletUseCase;
    private final OrderPaymentPersistencePort orderPaymentPersistencePort;
    private final PaymentFactory paymentFactory;

    private final PaymentMapper paymentMapper;


    /**
     * 상품의 값에 대한 첫번째 결제
     * 2025.08.19 수정 - 유저가 결제 할 수 있도록 변경, 관리자는 결제 요청 들어오면 바로 바로 구매 하도록 하고, 낙찰된 물건은 환불
     * orderitem uuid를 받아와서 paymnet 결제 작성 order를 따로 생성하지 않고 payment로 묶어서 처리
     * orderPayment를 생성
     *
     * @param userUuid
     * @return
     */
    @Override
    @Transactional
    public OrderPaymentResponseDto completeFirstPayment(UUID userUuid, OrderPaymentRequestDto requestDto) {

        Customer customer = findCustomerByUserUuid(customerPersistencePort.findByUserUuid(userUuid), userUuid);

        Wallet findWallet = findWalletByCustomerUuid(customer.getCustomerUuid());

        OrderPayment orderPayment = paymentFactory.createOrderFirstPayment(customer, findWallet, userUuid);
        OrderPayment savedOrderPayment = orderPaymentPersistencePort.saveOrderPayment(orderPayment);

        List<OrderItem> orderItems = orderItemPersistencePort.firstOrderItemPayment(customer.getCustomerUuid(), requestDto, savedOrderPayment.getPaymentUuid());orderItemPersistencePort.saveAllOrderItem(orderItems);

        //총 결제 금액 계산
        BigDecimal totalPrice = orderItems.stream().map(OrderItem::getPriceOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //로깅
        orderItems.forEach(item -> {
            log.debug("OrderItem ID: {}, PriceOriginal: {}", item.getOrderItemUuid(), item.getPriceOriginal());
        });


        log.debug("계산된 총 금액:{}", totalPrice);

        try {
            WalletResponseDto walletResponseDto = updateWalletUseCase.withdrawal(
                    customer.getCustomerUuid(),
                    findWallet.getWalletUuid(),
                    totalPrice
            );



            orderPayment.updateTotalAmount(totalPrice);

            return paymentMapper.completeOrderPayment(savedOrderPayment, walletResponseDto);
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (InsufficientBalanceException e) { // 특정 비즈니스 예외 처리 추가
            throw new PaymentProcessingException("잔액이 부족합니다.", e);
        } catch (Exception e) {
            log.error("Unexpected error during first payment for user {}: {}", userUuid, e.getMessage(), e);
            // 실패 시 처리는 트랜잭션 롤백으로 자동 처리되므로 명시적 호출은 불필요할 수 있습니다.
            throw new PaymentProcessingException("결제 완료 중 예상치 못한 오류가 발생했습니다.", e);
        }
    }

    /**
     * 관리자가 생성
     * 상품에 대한 배송비 결제
     * 유저 상품 상태변경, 그리고 요청 금액만 제시하면 될것 같으니까 리팩토링으로 간소화 시키고, 유저가 결제 할때 지갑, 등등을 검증하면 좋을것 같음
     * orderitem 에서 가져올 유저 wallet uuid를 일단 넣고, 나중에, 결제할때 검증용으로 쓰는게 더 안전할거 같기는한데,...
     *
     * @param adminUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public OrderPaymentResponseDto createSecondPayment(UUID adminUuid, OrderPaymentRequestDto requestDto) {

        User admin = findUserByUserUuid(adminUuid);
        admin.validateAdminRole();

        List<UUID> orderItemUuids = requestDto.getOrderItemUuids();
        UUID secondOrderItemUuid = orderItemUuids.get(0);
        OrderItem orderItem = orderItemPersistencePort.findByOrderItemUuid(secondOrderItemUuid)
                .orElseThrow(() -> new OrderItemNotFoundException());

        Customer customer = findCustomerByUserUuid(customerPersistencePort.findByCustomerUuid(orderItem.getCustomerUuid()), adminUuid);

        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());

        OrderPayment orderPayment = paymentFactory.createOrderSecondPayment(customer, requestDto.getDeliveryFee(), wallet, admin.getUserUuid());


        OrderPayment savedOrderPayment = orderPaymentPersistencePort.saveOrderPayment(orderPayment);
        orderItemPersistencePort.secondOrderItemPayment(orderItem.getCustomerUuid(), requestDto, savedOrderPayment.getPaymentUuid());

        return paymentMapper.createOrderPayment(savedOrderPayment);
    }


    /**
     * 유저가 결제된 상품에 대한 배송비 결제
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public OrderPaymentResponseDto completeSecondPayment(UUID userUuid, OrderPaymentDeliveryRequestDto requestDto) {

        Customer customer = findCustomerByUserUuid(customerPersistencePort.findByUserUuid(userUuid), userUuid);

        if (requestDto.getOrderPaymentUuids() == null || requestDto.getOrderPaymentUuids().isEmpty()) {
            throw new IllegalArgumentException("배송비를 지불할 결제를 선택하세요");
        }

        List<OrderPayment> orderPayments = orderPaymentPersistencePort.findByPaymentUuidInAndCustomerUuid(requestDto.getOrderPaymentUuids(), customer.getCustomerUuid());

        if (orderPayments.size() != requestDto.getOrderPaymentUuids().size()) {
            log.warn("요청된 결제와 조회된 결제가 수가 다름 - 요청: {}, 조회: {}",
                    requestDto.getOrderPaymentUuids().size(), orderPayments.size());
            throw new IllegalArgumentException("일부 결제를 찾을 수 없습니다");
        }

        List<OrderPayment> invalidPayments = orderPayments.stream()
                .filter(payment -> payment.getOrderPaymentStatus() != OrderPaymentStatus.PENDING)
                .toList();

        if (!invalidPayments.isEmpty()) {
            log.warn("결제 불가능한 상태의 결제가 발견 - 개수: {}", invalidPayments.size());
            throw new IllegalStateException("결제할 수 없는 상태의 결제가 포함되어 있습니다");
        }

        Wallet findWallet = findWalletByCustomerUuid(customer.getCustomerUuid());

//        List<OrderPayment> invalidPaymentsByWalletUuid = orderPayments.stream()
//                .filter(payment -> payment.getWalletUuid() != findWallet.getWalletUuid())
//                .toList();

        BigDecimal totalAmount = orderPayments.stream().map(OrderPayment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        try {

            WalletResponseDto wallet = updateWalletUseCase.withdrawal(
                    customer.getCustomerUuid(),
                    findWallet.getWalletUuid(),
                    totalAmount
            );

            orderPayments.forEach(OrderPayment::complete);

            List<OrderPayment> savedOrderPayments = orderPaymentPersistencePort.saveAll(orderPayments);

            return paymentMapper.completeOrderPayment(savedOrderPayments.get(0), wallet);
        } catch (InsufficientBalanceException e) {
            log.error("잔액 부족으로 인한 결제 실패 - 고객: {}, 요청 금액: {}",
                    customer.getCustomerUuid(), totalAmount);

            orderPayments.forEach(orderPayment -> orderPayment.markAsFailed("잔액 부족" + e.getMessage()));
            throw new OrderPaymentCompletionException("잔액이 부족합니다.");

        } catch (WalletNotActiveException e) {
            log.error("비활성화된 지갑으로 인한 결제 실패 - 고객: {}, 지갑: {}",
                    customer.getCustomerUuid(), findWallet.getWalletUuid(), e);

            orderPayments.forEach(orderPayment -> orderPayment.markAsFailed("비활성된 지갑: " + e.getMessage()));
            throw new WalletNotActiveException("지갑이 비활성 상태입니다", e);

        } catch (OrderPaymentNotFoundException | OrderPaymentStatusException e) {
            log.error("유효하지 않은 주문 결제 - 결제 UUID들: {}", requestDto.getOrderPaymentUuids(), e);
            throw e;
        } catch (OptimisticLockException e) {
            log.error("동시성 충돌 발생 - 고객: {}, 결제 UUID들: {}",
                    customer.getCustomerUuid(), requestDto.getOrderPaymentUuids(), e);
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (Exception e) {
            log.error("결제 완료 중 예상치 못한 오류 - 고객: {}, 결제 UUID들: {}",
                    customer.getCustomerUuid(), requestDto.getOrderPaymentUuids(), e);
            orderPayments.forEach(orderPayment -> orderPayment.markAsFailed("시스템 오류: " + e.getMessage()));
            throw new PaymentProcessingException("결제 완료 중 예상치 못한 오류가 발생했습니다", e);
        }
    }

    /**
     * 관리자가 오더 거래내역 조회
     *
     * @param adminUuid
     * @param paymentUuid
     * @return
     */
    @Override
    public OrderPaymentResponseDto getOrderPaymentInfoByAdmin(UUID adminUuid, UUID paymentUuid) {
        User admin = findUserByUserUuid(adminUuid);
        admin.validateAdminRole();

        OrderPayment findByorderPayment = orderPaymentPersistencePort.findByPaymentUuid(paymentUuid)
                .orElseThrow(() -> new OrderPaymentNotFoundException(String.format("OrderJpaEntity payment not found for payment uuid: %s", paymentUuid)));

        return paymentMapper.orderPaymentInfo(findByorderPayment);
    }


    /**
     * 유저가 본인의 거래내역 조회
     *
     * @param userUuid
     * @param paymentUuid
     * @return
     */
    @Override
    public OrderPaymentResponseDto getOrderPaymentInfo(UUID userUuid, UUID paymentUuid) {
        Customer customer = findCustomerByUserUuid(customerPersistencePort.findByUserUuid(userUuid), userUuid);
        OrderPayment findByOrderPayment = findOrderPaymentByCustomerUuidAndPaymentUuid(customer.getCustomerUuid(), paymentUuid);
        return paymentMapper.orderPaymentInfo(findByOrderPayment);
    }

    /**
     * OrderJpaEntity payment 상태, 조건 별 list
     *
     * @param userUuid
     * @param status
     * @param paymentType
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderPaymentResponseDto> getOrderPaymentsByStatusAndType(UUID userUuid, OrderPaymentStatus status, PaymentType paymentType, Pageable pageable) {
        Customer customer = findCustomerByUserUuid(customerPersistencePort.findByUserUuid(userUuid), userUuid);

        Page<OrderPayment> orderPaymentsByPending = orderPaymentPersistencePort.findAllByCustomerUuidAndOrderPaymentStatusAndPaymentTypeOrderByCreatedAtDesc(customer.getCustomerUuid(), status, paymentType, pageable);

        return orderPaymentsByPending.map(paymentMapper::orderPaymentInfo);
    }


    /**
     * 본인 유저의 거래내역 조회
     *
     * @param userUuid
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderPaymentResponseDto> getOrderPayments(UUID userUuid, Pageable pageable) {

        User user = findUserByUserUuid(userUuid);

        Customer customer = findCustomerByUserUuid(customerPersistencePort.findByUserUuid(user.getUserUuid()), user.getUserUuid());
        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());
        Page<OrderPayment> orderPayments = orderPaymentPersistencePort.findAllByCustomerUuid(customer.getCustomerUuid(), pageable);

        return orderPayments.map(orderPayment -> paymentMapper.orderPaymentDetailsInfo(orderPayment, wallet.getBalance()));
    }

    @Override
    public OrderPaymentCountResponseDto getOrderPaymentDashboardCount(UUID userUuid) {
        Customer customer = findCustomerByUserUuid(customerPersistencePort.findByUserUuid(userUuid), userUuid);

        int firstPending = orderPaymentPersistencePort.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.PENDING, PaymentType.PRODUCT_PAYMENT);
        int firstCompleted = orderPaymentPersistencePort.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.COMPLETED, PaymentType.PRODUCT_PAYMENT);
        int secondPending = orderPaymentPersistencePort.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.PENDING, PaymentType.SHIPPING_PAYMENT);
        int secondCompleted = orderPaymentPersistencePort.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.PENDING, PaymentType.SHIPPING_PAYMENT);

        return paymentMapper.orderPaymentDashboardCount(firstPending, firstCompleted, secondPending, secondCompleted);
    }


    //helper method
    private Wallet findWalletByCustomerUuid(UUID customerUuid) {
        return walletPersistencePort.findByCustomerUuid(customerUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found exception for customerUuid: %s", customerUuid)));
    }

    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found exception for userUuid: %s", userUuid)));
    }

    private OrderPayment findOrderPaymentByCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid) {
        return orderPaymentPersistencePort.findByCustomerUuidAndPaymentUuid(customerUuid, paymentUuid)
                .orElseThrow(() -> new OrderPaymentNotFoundException(String.format("OrderJpaEntity payment not found for customer uuid: %s and payment uuid: %s ", customerUuid, paymentUuid)));
    }

    private Customer findCustomerByUserUuid(Optional<Customer> customerPersistencePort, UUID userUuid) {
        return customerPersistencePort
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
    }
}
