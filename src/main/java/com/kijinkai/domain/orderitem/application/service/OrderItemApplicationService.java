package com.kijinkai.domain.orderitem.application.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.order.application.validator.OrderValidator;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.adapter.out.persistence.repostiory.OrderItemSearchCondition;
import com.kijinkai.util.BusinessCodeType;
import com.kijinkai.util.GenerateBusinessItemCode;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.dto.*;
import com.kijinkai.domain.orderitem.application.port.in.CreateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.DeleteOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.GetOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.mapper.OrderItemMapper;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.application.validator.OrderItemValidator;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemValidateException;
import com.kijinkai.domain.orderitem.domain.factory.OrderItemFactory;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.application.port.in.GetWalletUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderItemApplicationService implements CreateOrderItemUseCase, GetOrderItemUseCase, UpdateOrderItemUseCase, DeleteOrderItemUseCase {

    private final CustomerPersistencePort customerPersistencePort;
    private final OrderItemPersistencePort orderItemPersistencePort;

    private final OrderItemValidator orderItemValidator;
    private final OrderItemFactory orderItemFactory;
    private final OrderItemMapper orderItemMapper;

    //util
    private final GenerateBusinessItemCode generateBusinessItemCode;


    //outer
    private final UserPersistencePort userPersistencePort;
    private final PriceCalculationService priceCalculationService;


    /**
     * 유저로 부터 링크, 가격, 수량, 메모 등을 받아서 생성  -- 필요없는 로직 삭제예정
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
        String orderItemCode = generateBusinessItemCode.generateBusinessCode(customer.getCustomerUuid().toString(), BusinessCodeType.ORI);

        return orderItemFactory.createOrderItem(customer, requestDto, orderItemCode);
    }


    /**
     * 요청 상품들 저장
     *
     * @param userUuid
     * @param orderItemRequestDtos
     * @return
     */
    @Transactional
    @Override
    public List<UUID> createOrderItems(UUID userUuid, List<OrderItemRequestDto> orderItemRequestDtos) {

        // 주문상품 수량 검증
        if (orderItemRequestDtos == null || orderItemRequestDtos.isEmpty()) {
            throw new IllegalArgumentException("Not found order item");
        }

        // 유저Uuid로 customer 정보 호출
        Customer customer = findCustomerByUserUuid(userUuid);


        // 리스트 생성
        List<OrderItem> orderItems = orderItemRequestDtos.stream()
                .map(dto -> {

                    // 상품주문 코드생성
                    String individualCode = generateBusinessItemCode.generateBusinessCode(userUuid.toString(), BusinessCodeType.ORI);

                    return orderItemFactory.createOrderItem(customer, dto, individualCode);
                })
                .toList();

        // 리스트로 생성된것을 저장
        List<OrderItem> savedAllOrderItem = orderItemPersistencePort.saveAllOrderItem(orderItems);


        return savedAllOrderItem.stream().map(OrderItem::getOrderItemUuid).toList();
    }


    // 일단 사용하고 리펙토링 할때 위에 코드랑 유사함으로 결합 고려해야함
    @Override
    @Transactional
    public List<OrderItem> secondOrderItemPayment(UUID customerUuid, OrderPaymentRequestDto request, UUID deliveryPaymentUuid) {

        List<OrderItem> orderItems = orderItemPersistencePort.findAllByOrderItemUuidIn(request.getOrderItemUuids());
        orderItemValidator.validateOrderItems(orderItems, request.getOrderItemUuids(), OrderItemStatus.PRODUCT_PAYMENT_COMPLETED);
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


    @Override
    public OrderItemResponseDto getOrderItemInfo(UUID userUuid, UUID orderItemUuid) {

        // 유저 uuid 로 구매자 조회
        Customer customerJpaEntity = findCustomerByUserUuid(userUuid);

        // customerUuid, orderItemUuid, status 로 각 status 구매정보 조회
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

//        orderValidator.requireDraftOrderStatus(orderItem.getOrder());

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
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid: %s", userUuid)));
        user.validateAdminRole();

        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);

        orderItem.updateOrderItem(updateDto);

        return orderItem;
    }


    // ---- 업데이트. ----

    @Override
    @Transactional
    public String rejectOrderItem(UUID userAdminUuid, UUID orderItemUuid, OrderItemRejectRequestDto requestDto) {

        if (requestDto.getRejectReason() == null || requestDto.getRejectReason().isEmpty()){
            throw new OrderItemValidateException("이유 없이 거절은 할수 없습니다;");
        }

        // 관리자 검증 및 조회
        User userAdmin = findUserByUserUuid(userAdminUuid);
        userAdmin.validateAdminRole();

        // 상품 조회
        OrderItem orderItem = findOrderItemByOrderItemUuid(orderItemUuid);

        // 거절 상태변경 및 이유 추가
        orderItem.reject(requestDto.getRejectReason());

        // 저장
        OrderItem savedOrderitem = orderItemPersistencePort.saveOrderItem(orderItem);

        return savedOrderitem.getOrderItemCode();
    }


    @Override
    @Transactional
    public void delivered(UUID shipmentUuid) {
        List<OrderItem> orderItems = orderItemPersistencePort.findAllByShipmentUuidAndOrderItemStatus(shipmentUuid, OrderItemStatus.IN_TRANSIT);
        for (OrderItem orderItem : orderItems) {
            orderItem.delivered();
        }

        orderItemPersistencePort.saveAllOrderItem(orderItems);
    }


    @Override
    @Transactional
    public void startDelivery(UUID shipmentUuid) {

        List<OrderItem> orderItems = orderItemPersistencePort.findAllByShipmentUuidAndOrderItemStatus(shipmentUuid, OrderItemStatus.DELIVERY_FEE_PAYMENT_COMPLETED);
        for (OrderItem orderItem : orderItems) {
            orderItem.startDelivery();
        }

        orderItemPersistencePort.saveAllOrderItem(orderItems);
    }


    /**
     * 결제 완료 상태변화
     *
     * @param customerUuid
     * @param shipmentUuid
     */
    @Override
    @Transactional
    public void completedDeliveryPayment(UUID customerUuid, List<UUID> shipmentUuids) {

        List<OrderItem> orderItems = orderItemPersistencePort.findAllByCustomerUuidAndOrderItemStatusAndShipmentUuidIn(customerUuid, OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST, shipmentUuids);
        for (OrderItem orderItem : orderItems) {
            orderItem.completedDeliveryPayment();
        }

        orderItemPersistencePort.saveAllOrderItem(orderItems);
    }


    @Override
    @Transactional
    public void assignToShipment(List<String> orderItemCodes, UUID shipmentUuid) {

        // dliveryUuid로 조회된 상품을 orderitemCodes를 통해 적정 수량을 가져온다.
        // 상자에 넣게 가져온다. 전체가 아님으로.

        List<OrderItem> orderItems = orderItemPersistencePort.findAllByOrderItemCodeInAndOrderItemStatus(orderItemCodes, OrderItemStatus.PRODUCT_CONSOLIDATING);


        orderItems.forEach(
                orderItem -> {
                    orderItem.requestDeliveryFee();
                    orderItem.addShipmentUuid(shipmentUuid);
                }
        );

        orderItemPersistencePort.saveAllOrderItem(orderItems);
    }


    /**
     * 상품 배송요청
     *
     * @param orderItemCods
     * @param deliveryUuid
     */
    @Override
    @Transactional
    public void registerDeliveryToOrderItems(List<String> orderItemCods, UUID deliveryUuid) {
        List<OrderItem> orderItems = orderItemPersistencePort.findAllByOrderItemCodeInAndOrderItemStatus(orderItemCods, OrderItemStatus.LOCAL_DELIVERY_COMPLETED);
        orderItems.forEach(
                orderItem -> {
                    orderItem.addDeliveryUuid(deliveryUuid);
                    orderItem.startConsolidation();
                }
        );
        orderItemPersistencePort.saveAllOrderItem(orderItems);
    }


    @Override
    public void requestPhotoInspection(List<OrderItem> orderItems, Map<String, Boolean> inspectionRequestMap) {
        orderItems.forEach(orderItem ->
        {
            Boolean requested = inspectionRequestMap.get(orderItem.getOrderItemCode());
            if (Boolean.TRUE.equals(requested)) {
                orderItem.requestPhotoInspection();
            }
        });
    }

    @Override
    @Transactional
    public void updateOrderItemStatusByFirstComplete(List<OrderItem> orderItems, Map<String, Boolean> inspectionRequestMap) {

        orderItems.forEach(orderItem -> {
            Boolean requested = inspectionRequestMap.get(orderItem.getOrderItemCode());
            if (Boolean.TRUE.equals(requested)) {
                orderItem.requestPhotoInspection();
            }
            orderItem.completeFirstOrderItemPayment();
        });
    }


    /**
     * 구매자 요청을 관리자가 승인
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public List<String> processFirstOderItem(UUID userUuid, OrderItemApprovalRequestDto requestDto) {

        // 토큰으로 부터 유저조회
        User user = findUserByUserUuid(userUuid);

        // 관리자 권한 검증
        user.validateAdminRole();

        // 상태별 조회
        List<OrderItem> orderItems = orderItemPersistencePort.findAllByOrderItemStatusAndOrderItemCodeIn(OrderItemStatus.PENDING, requestDto.getOrderItemCodes());

        // 승인처리
        for (OrderItem orderItem : orderItems) {
            orderItem.approveOrderItem();
        }

        //저장
        List<OrderItem> savedOrderItems = orderItemPersistencePort.saveAllOrderItem(orderItems);


        return savedOrderItems.stream().map(OrderItem::getOrderItemCode).toList();
    }


    @Override
    @Transactional
    public List<String> completeLocalDelivery(UUID userUuid, OrderItemApprovalRequestDto requestDto) {

        // 토큰으로 부터 유저권한이 관리자인지 검증
        User user = findUserByUserUuid(userUuid);
        user.validateAdminRole();

        //1차 결제 완료 상품 조회
        List<OrderItem> orderItems = orderItemPersistencePort.findAllByOrderItemStatusAndOrderItemCodeIn(OrderItemStatus.PRODUCT_PAYMENT_COMPLETED, requestDto.getOrderItemCodes());

        //국내 배송완료로 상태변경
        for (OrderItem orderItem : orderItems) {
            orderItem.completedLocalDelivery();
        }

        //상태변경된 주문상품 저장
        List<OrderItem> savedOrderItems = orderItemPersistencePort.saveAllOrderItem(orderItems);


        return savedOrderItems.stream().map(OrderItem::getOrderItemCode).toList();
    }


    // ----- 조회. -----

    /**
     * 관리자 -> 상태별, 주문상품번호, 날짜별 조회
     *
     * @param userAdminUuid
     * @param status
     * @return
     */
    @Override
    public Page<OrderItemResponseDto> getAdminOrderItemsByStatus(UUID userAdminUuid, OrderItemStatus status, String orderItemCode, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        User userAdmin = findUserByUserUuid(userAdminUuid);
        userAdmin.validateAdminRole();


        // 검색 조건 설정
        OrderItemSearchCondition condition = new OrderItemSearchCondition();
        condition.setStatus(status);
        condition.setOrderItemCode(orderItemCode);
        condition.setStartDate(startDate);
        condition.setEndDate(endDate);


        //조회

        Page<OrderItem> orderItems = orderItemPersistencePort.searchAdminOrderItemsByStatus(condition, pageable);

        return orderItems.map(orderItemMapper::toResponseDto);
    }


    @Override
    public List<OrderItemResponseDto> getOrderItemByDeliveryUuid(UUID userUuid, UUID deliveryUuid) {

        // 관리자 검증
        User user = findUserByUserUuid(userUuid);
        user.validateAdminRole();

        List<OrderItem> orderItems = orderItemPersistencePort.findAllByDeliveryUuid(deliveryUuid);

        return orderItems.stream().map(orderItemMapper::toProductDetailDto).toList();
    }


    @Override
    public List<OrderItem> getOrderItemsByCodeAndStatus(List<String> orderItemCode, OrderItemStatus status) {
        return orderItemPersistencePort.findAllByOrderItemCodeInAndOrderItemStatus(orderItemCode, status);
    }


    /**
     * 여러 Status 별 구매상품 조회
     *
     * @param uuid
     * @param orderItemStatuses
     * @return
     */
    @Override
    public List<OrderItem> getOrderItemsByCustomerAndOrderItemsStatus(UUID userUuid, List<OrderItemStatus> orderItemStatuses) {

        Customer customer = findCustomerByUserUuid(userUuid);

        return orderItemPersistencePort.findAllByCustomerUuidAndOrderItemStatusIn(customer.getCustomerUuid(), orderItemStatuses);

    }


    /**
     * 구매자 상태별 갯수
     *
     * @param userUuid
     * @param orderItemStatus
     * @return
     */
    @Override
    public int countOrderItemsByStatus(UUID userUuid, OrderItemStatus orderItemStatus) {

        Customer customer = findCustomerByUserUuid(userUuid);
        return orderItemPersistencePort.findOrderItemCountByStatus(customer.getCustomerUuid(), orderItemStatus);
    }


    @Override
    public int countOrderItemByStatusIn(UUID userUuid, List<OrderItemStatus> orderItemStatus) {
        Customer customer = findCustomerByUserUuid(userUuid);
        return orderItemPersistencePort.findOrderItemCountByStatusIn(customer.getCustomerUuid(), orderItemStatus);
    }

    /**
     * 구매자의 각 상태별 조회
     *
     * @param userUuid
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderItemResponseDto> getOrderItemByStatus(UUID userUuid, OrderItemStatus orderItemStatus, Pageable pageable) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Page<OrderItem> orderItems = orderItemPersistencePort.findAllByCustomerUuidAndOrderItemStatusOrderByCreatedAtDesc(customer.getCustomerUuid(), orderItemStatus, pageable);
        return orderItems.map(orderItemMapper::toProductDetailDto);
    }


    // -----  삭제 -----

    @Transactional
    @Override
    public void deleteByOrderItemCode(UUID userUuid, String orderItemCode) {

        // 유저 Uuid로 구매자 조회
        Customer customer = findCustomerByUserUuid(userUuid);

        // 구매자 uuid, orderItemCode 로 orderItem 조회
        OrderItem orderItem = orderItemPersistencePort.findByCustomerUuidAndOrderItemCode(customer.getCustomerUuid(), orderItemCode)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", userUuid)));

        if (!orderItem.getOrderItemStatus().equals(OrderItemStatus.PENDING)) {
            throw new OrderItemValidateException("대기중인 상품만 삭제할수 있습니다.");
        }

        // 삭제
        orderItemPersistencePort.deleteOrderItem(orderItem);
    }

    // ----- helper -----

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", userUuid)));
    }


    public OrderItem findOrderItemByOrderItemUuid(UUID orderItemUuid) {
        return orderItemPersistencePort.findByOrderItemUuid(orderItemUuid)
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("OrderItem not found for orderItemUuid: %s", orderItemUuid)));
    }

    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user uuid: %s", userUuid)));
    }

}
