package com.kijinkai.domain.payment.application.service;

import com.kijinkai.domain.coupon.application.port.in.usercoupon.UpdateUserCouponUseCase;
import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentDeliveryRequestDto;
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
import com.kijinkai.domain.payment.domain.exception.OrderPaymentNotFoundException;
import com.kijinkai.domain.payment.domain.exception.PaymentProcessingException;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import com.kijinkai.domain.shipment.repository.ShipmentEntityRepository;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.transaction.service.TransactionService;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.application.port.in.UpdateWalletUseCase;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.application.service.WalletApplicationService;
import com.kijinkai.domain.wallet.domain.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import com.kijinkai.util.BusinessCodeType;
import com.kijinkai.util.GenerateBusinessItemCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderPaymentApplicationService implements CreateOrderPaymentUseCase, GetOrderPaymentUseCase, UpdateOrderPaymentUseCase, DeleteOrderPaymentUseCase {

    private final CustomerPersistencePort customerPersistencePort;
    private final WalletPersistencePort walletPersistencePort;
    private final OrderPaymentPersistencePort orderPaymentPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final ShipmentEntityRepository shipmentEntityRepository;

    private final UpdateOrderItemUseCase updateOrderItemUseCase;
    private final UpdateWalletUseCase updateWalletUseCase;
    private final WalletApplicationService walletApplicationService;


    private final UpdateUserCouponUseCase updateUserCouponUseCase;
    private final TransactionService transactionService;
    private final PaymentFactory paymentFactory;
    private final PaymentMapper paymentMapper;
    private final GenerateBusinessItemCode generateBusinessItemCode;


    private final BigDecimal AGENCY_FEE = BigDecimal.valueOf(0.1);


    @Override
    @Transactional
    public OrderPayment saveOrderItem(Customer customer, BigDecimal exchangedAmount, BigDecimal discountAmount, BigDecimal finalPaymentAmount, UUID userCouponUuid){

        // 지갑에서 출금. // db
        WalletResponseDto wallet = updateWalletUseCase.withdrawal(customer.getCustomerUuid(), finalPaymentAmount);

        // 결제코드 생성 // db
        String paymentCode = generateBusinessItemCode.generateBusinessCode(customer.getUserUuid().toString(), BusinessCodeType.ORP);

        // 쿠폰 상태변경
        if (userCouponUuid != null){
            updateUserCouponUseCase.useCoupon(customer.getUserUuid(), userCouponUuid, discountAmount);
        }

        //생성 // db
        OrderPayment orderPayment = paymentFactory.createProductPayment(
                customer.getCustomerUuid(),
                wallet.getWalletUuid(),
                paymentCode,
                exchangedAmount,
                discountAmount
        );

        //저장 // db
        OrderPayment savedOrderPayment = orderPaymentPersistencePort.saveOrderPayment(orderPayment);

        //내역 저장  // db
        transactionService.createAccountHistory(
                customer.getCustomerUuid(),wallet.getWalletUuid(),
                TransactionType.ORDER,
                savedOrderPayment.getOrderPaymentCode(),
                savedOrderPayment.getFinalPaymentAmount(),
                TransactionStatus.COMPLETED
        );

        return savedOrderPayment;
    }

    /*
    배송비 결제요청 생성
     */
    @Override
    @Transactional
    public OrderPayment createDeliveryPayment(String userUuid, BigDecimal deliveryFee, UUID deliveryUuid, UUID customerUuid) {

        // 유저의 uuid를 가져와야함
        String orderCode = generateBusinessItemCode.generateBusinessCode(userUuid, BusinessCodeType.ORP);

        //지갑 조회
        Wallet wallet = findWalletByCustomerUuid(customerUuid);

        //생성
        OrderPayment deliveryPayment = paymentFactory.createDeliveryPayment(
                orderCode,
                deliveryFee,
                deliveryUuid,
                customerUuid,
                wallet.getWalletUuid()
        );

        //저장
        return orderPaymentPersistencePort.saveOrderPayment(deliveryPayment);
    }


    @Override
    @Transactional
    public OrderPaymentResponseDto paymentDeliverFee(UUID userUuid, OrderPaymentDeliveryRequestDto requestDto){
        return executeWithOptimisticLockRetry(()
                -> processDeliveryPayment(userUuid, requestDto));
    }

    /**
     * 유저가 결제된 상품에 대한 배송비 결제. - 수정중.
     * Box 코드를 선택 후에 -> 결제(대행수수료 10%) -> 지갑에서 돈이 차감됨
     *  대행수수료는 뺴고 요청 -> 고시환률로 수익극대
     *. 전면 수정필요함
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Transactional
    private OrderPaymentResponseDto processDeliveryPayment(UUID userUuid, OrderPaymentDeliveryRequestDto requestDto) {

//        // userUuid로 결제하는 customer 조회
//        Customer customer = customerPersistencePort.findByUserUuid(userUuid)
//                .orElseThrow(() -> new CustomerNotFoundException("Not found customer"));
//
//        if (requestDto.getBoxCodes() == null || requestDto.getBoxCodes().isEmpty()) {
//            throw new OrderItemValidateException("결제할 orderItem 이 없습니다.");
//        }
//
//        // 주문결제 식별 코드생성
//        String orderPaymentCode = generateBusinessItemCode.generateBusinessCode(userUuid.toString(), BusinessCodeType.ORP);
//
//        // 체크된 박스를 조회 및 검증
//        List<ShipmentEntity> shipmentsByPending = shipmentEntityRepository.findAllByCustomerUuidAndShipmentStatusAndBoxCodeIn(customer.getCustomerUuid(), ShipmentStatus.PAID, requestDto.getBoxCodes());
//        if (shipmentsByPending.isEmpty()){
//            throw new IllegalArgumentException("결제가능한 배송박스를 찾을 수 없습니다.");
//        }
//
//        if (shipmentsByPending.size() != requestDto.getBoxCodes().size()){
//            throw new IllegalStateException("요청하신 박스 중 이미 결제되었거나 유효하지 않는 박스가 있습니다.");
//        }
//
//
//        List<UUID> shipmentUuids = shipmentsByPending.stream().map(ShipmentEntity::getShipmentUuid).toList();
//
//        // 체크된 박스의 금액 총합
//        BigDecimal totalShipmentFee = shipmentsByPending.stream()
//                .map(ShipmentEntity::getShippingFee)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // 대행수수료 10% 포함
//        BigDecimal paymentAmount = totalShipmentFee.add(totalShipmentFee.multiply(AGENCY_FEE));
//
//        //지갑에서 차감
//        WalletResponseDto wallet = walletApplicationService.withdrawal(customer.getCustomerUuid(), paymentAmount);
//
//        //주문 거래 내역 저장
//        OrderPayment orderSecondPayment = paymentFactory.createOrderSecondPayment(
//                customer.getCustomerUuid(),
//                paymentAmount,
//                orderPaymentCode,
//                wallet.getWalletUuid()
//        );
//
//        // orderitem 배송비 결제완료로 상태변화
//        updateOrderItemUseCase.completedDeliveryPayment(customer.getCustomerUuid(), shipmentUuids);
//
//        //박스에 order paymentUuid 저장
//        OrderPayment savedOrderpayment = orderPaymentPersistencePort.saveOrderPayment(orderSecondPayment);
//
//        //shipment에 결제 orderPayment Uuid 저장
//        shipmentService.registerOrderPaymentToShipment(shipmentsByPending, savedOrderpayment.getPaymentUuid());
//

//        return paymentMapper.deliveryPaymentResponse(savedOrderpayment, paymentAmount, wallet.getBalance().subtract(paymentAmount));
        return null;
    }




    //  --- 업데이트

    @Transactional
    public void failOrderPayment(UUID customerUuid, UUID orderPaymentUuid){

        // 결제 조회
        OrderPayment orderPayment = findOrderPaymentByCustomerUuidAndPaymentUuid(customerUuid, orderPaymentUuid);

        walletApplicationService.refund(orderPayment.getCustomerUuid(), orderPayment.getWalletUuid(), orderPayment.getPaymentAmount());

        orderPayment.fail();
    }


    //  ---- 조회.



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

    //결제 재시도 (동시성 해결)
    public OrderPaymentResponseDto executeWithOptimisticLockRetry(Supplier<OrderPaymentResponseDto> operation) {
        int maxRetries = 3;
        for (int retryCount = 1; retryCount <= maxRetries; retryCount++) {
            try {
                return operation.get();
            } catch (OptimisticLockingFailureException e) {
                if (retryCount == maxRetries) {
                    log.error("Fail retry failed for optimistic lock");
                    throw new PaymentProcessingException("동시 접속자가 많아 실패했습니다.");
                }

                long waitTime = (long) (Math.pow(2, retryCount) * 100);
                waitForRetry(waitTime);
                log.warn("Retry {}/{} due to conflict", retryCount, maxRetries);
            }
        }

        throw new PaymentProcessingException("Unexpected error in payment completion");
    }

    private void waitForRetry(long millis){
        try {
            Thread.sleep(millis);
        }catch (InterruptedException ie){
            Thread.currentThread().interrupt();
            throw new PaymentProcessingException("결제 처리 중 인터럽스가 발생하였습니다.");
        }
    }


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
