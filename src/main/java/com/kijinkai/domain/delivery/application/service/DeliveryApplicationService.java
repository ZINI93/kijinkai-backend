package com.kijinkai.domain.delivery.application.service;

import com.kijinkai.domain.address.application.port.out.AddressPersistencePort;
import com.kijinkai.domain.address.domain.exception.AddressNotFoundException;
import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.adpater.out.persistence.repository.DeliverySearchCondition;
import com.kijinkai.domain.delivery.application.dto.request.DeliveryCancelRequestDto;
import com.kijinkai.domain.delivery.application.dto.response.DeliveryCountResponseDto;
import com.kijinkai.domain.delivery.application.dto.request.DeliveryRequestDto;
import com.kijinkai.domain.delivery.application.dto.request.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.application.dto.response.DeliveryResponseDto;
import com.kijinkai.domain.delivery.domain.event.DeliveryCancelledEvent;
import com.kijinkai.domain.delivery.domain.exception.DeliveryInvalidException;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.application.in.CreateDeliveryUseCase;
import com.kijinkai.domain.delivery.application.in.DeleteDeliveryUseCase;
import com.kijinkai.domain.delivery.application.in.GetDeliveryUseCase;
import com.kijinkai.domain.delivery.application.in.UpdateDeliveryUseCase;
import com.kijinkai.domain.delivery.application.mapper.DeliveryMapper;
import com.kijinkai.domain.delivery.application.out.DeliveryPersistencePort;
import com.kijinkai.domain.delivery.application.validator.DeliveryValidator;
import com.kijinkai.domain.delivery.domain.exception.DeliveryCreationException;
import com.kijinkai.domain.delivery.domain.exception.DeliveryNotFoundException;
import com.kijinkai.domain.delivery.domain.exception.DeliveryUpdateException;
import com.kijinkai.domain.delivery.domain.factory.DeliveryFactory;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import com.kijinkai.domain.order.application.validator.OrderValidator;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import com.kijinkai.domain.orderitem.application.mapper.OrderItemMapper;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.port.in.orderPayment.CreateOrderPaymentUseCase;
import com.kijinkai.domain.payment.application.port.out.OrderPaymentPersistencePort;
import com.kijinkai.domain.payment.domain.exception.OrderPaymentNotFoundException;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import com.kijinkai.domain.shipment.entity.ShipmentEntity;
import com.kijinkai.domain.shipment.repository.ShipmentEntityRepository;
import com.kijinkai.domain.shipment.service.ShipmentService;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DeliveryApplicationService implements CreateDeliveryUseCase, DeleteDeliveryUseCase, GetDeliveryUseCase, UpdateDeliveryUseCase {


    private final DeliveryPersistencePort deliveryPersistencePort;

    private final DeliveryFactory deliveryFactory;
    private final DeliveryMapper deliveryMapper;
    private final DeliveryValidator deliveryValidator;

    //외부
    private final CustomerPersistencePort customerPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final AddressPersistencePort addressPersistencePort;
    private final UpdateOrderItemUseCase updateOrderItemUseCase;

    private final OrderPaymentPersistencePort orderPaymentPersistencePort;  // 수정필요
    private final OrderItemPersistencePort orderItemPersistencePort;
    private final OrderItemMapper orderItemMapper;
    private final CreateOrderPaymentUseCase createOrderPaymentUseCase;
    private final ShipmentEntityRepository shipmentEntityRepository;
    private final ApplicationEventPublisher eventPublisher;


    /**
     * 고객의 배송요청
     *
     * @param userUuid
     * @param addressUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public UUID requestDelivery(UUID userUuid, UUID addressUuid, DeliveryRequestDto requestDto) {

        // 구매자 조회
        Customer customer = findCustomerByUserUuid(userUuid);

        // 구매자의 주소 조회
        Address address = addressPersistencePort.findByCustomerUuidAndAddressUuid(customer.getCustomerUuid(), addressUuid)
                .orElseThrow(() -> new AddressNotFoundException(String.format("Address Not found for addressUuid: %s", addressUuid)));

        if (address == null) {
            throw new IllegalArgumentException("주소를 작성하지 않는 상태에서 배송신청을 할수 없습니다.");
        }

        // 배달 작성
        Delivery delivery = deliveryFactory.createDelivery(customer.getCustomerUuid(), address, requestDto);

        // 배달 저장
        Delivery savedDelivery = deliveryPersistencePort.saveDelivery(delivery);

        // 주문상품들 상태변경
        updateOrderItemUseCase.registerDeliveryToOrderItems(requestDto.getOrderItemCodes(), savedDelivery.getDeliveryUuid());

        return savedDelivery.getDeliveryUuid();
    }


    /**
     * @param userUuid
     * @param orderPaymentUuid - 2차 결제 orderPayment. -- 수정필요함
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public DeliveryResponseDto createDelivery(UUID userUuid, UUID orderPaymentUuid, DeliveryRequestDto requestDto) {

        log.info("Creating delivery for user uuid: {}", userUuid);

        User user = findUserByUserUuid(userUuid);
        user.validateAdminRole();

        OrderPayment secondOrderPayment = orderPaymentPersistencePort.findByPaymentUuid(orderPaymentUuid)
                .orElseThrow(() -> new OrderPaymentNotFoundException(String.format("OrderJpaEntity payment not found exception for orderPaymentUuid: %s", orderPaymentUuid)));

        Customer customer = customerPersistencePort.findByCustomerUuid(secondOrderPayment.getCustomerUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found exception for customer uuid: %s", secondOrderPayment.getCustomerUuid())));

        Address address = addressPersistencePort.findByCustomerUuid(customer.getCustomerUuid())
                .orElseThrow(() -> new AddressNotFoundException(String.format("Address not found for customerUuid: %s", customer.getCustomerUuid())));

        try {

            return null;
        } catch (Exception e) {
            log.error("Failed to create delivery for user uuid: {}", userUuid);
            throw new DeliveryCreationException("Failed to create delivery", e);
        }
    }


    /**
     * 견적서에 적힌 물품 구매가 완료된 후 해당 나라로 배송을 시작
     *
     * @param userUuid
     * @param deliveryUuid
     * @return 배송시작 응답 DTO
     */
    @Override
    @Transactional
    public DeliveryResponseDto shipDelivery(UUID userUuid, UUID deliveryUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);
        User user = findUserByUserUuid(userUuid);
        user.validateAdminRole();

        Delivery delivery = findDeliveryByCustomerUuidAndDeliveryUuid(deliveryUuid, customer);

        deliveryValidator.requirePendingStatus(delivery);

        delivery.updateDeliveryStatus(DeliveryStatus.SHIPPED);

        Delivery savedDelivery = deliveryPersistencePort.saveDelivery(delivery);

        return deliveryMapper.toResponse(savedDelivery);
    }

    /*
    관리자가 입금요청
     */
    @Override
    @Transactional
    public DeliveryResponseDto requestDeliveryPayment(UUID userAdminUuid, UUID deliveryUuid) {

        // 관리자 검증
        User user = findUserByUserUuid(userAdminUuid);
        user.validateAdminRole();

        //상태변경 및 검증
        Delivery delivery = findDeliveryByDeliveryUuid(deliveryUuid);

        Customer customer = customerPersistencePort.findByCustomerUuid(delivery.getCustomerUuid())
                .orElseThrow(() -> new CustomerNotFoundException("고객을 찾을 수 없습니다."));

        // 박스 조회해서 가격 합산
        List<ShipmentEntity> shipments = shipmentEntityRepository.findAllByDeliveryUuid(deliveryUuid);

        if (shipments.isEmpty()) {
            throw new IllegalStateException("포장된 박스가 없어 배송비를 청구할 수 없습니다.");
        }

        // 요금 합산
        BigDecimal totalShipmentFee = shipments.stream()
                .map(ShipmentEntity::getShippingFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        delivery.changeRequestPayment(totalShipmentFee);

        //요청 orderPayment생성
        createOrderPaymentUseCase.createDeliveryPayment(customer.getUserUuid().toString(), totalShipmentFee, deliveryUuid, customer.getCustomerUuid());

        // 아이템 상태변경
        updateOrderItemUseCase.requestDeliveryPayment(deliveryUuid);

        Delivery savedDelivery = deliveryPersistencePort.saveDelivery(delivery);


        // 박스 상태변경 필요함

        // 저장
        return deliveryMapper.toPaymentResponse(savedDelivery);
    }


    // --- 조회. --

    @Override
    public Page<DeliveryResponseDto> getDeliveriesByAdmin(UUID userAdminUuid, String name, String phoneNumber, DeliveryStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        // Admin 검증
        User user = findUserByUserUuid(userAdminUuid);
        user.validateAdminRole();

        // 조건 매핑
        DeliverySearchCondition condition = DeliverySearchCondition.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Page<Delivery> deliveries = deliveryPersistencePort.searchDeliveries(condition, pageable);
        List<UUID> customerUuidsByDelivery = deliveries.stream().map(Delivery::getCustomerUuid).distinct().toList();

        if (deliveries.isEmpty()) {
            return null;
        }


        Map<UUID, Customer> customerMap = customerPersistencePort.findAllByCustomerUuidIn(customerUuidsByDelivery)
                .stream()
                .collect(Collectors.toMap(
                        Customer::getCustomerUuid,
                        c -> c,
                        (e, r) -> e
                ));

        return deliveries.map(
                delivery -> {
                    Customer customer = customerMap.get(delivery.getCustomerUuid());
                    return deliveryMapper.toAdminDeliveriesResponse(delivery, customer);
                }
        );
    }

    @Override
    public DeliveryResponseDto getRequestDeliveryOrderItemByAdmin(UUID userAdminUuid, UUID deliveryUuid, Pageable pageable) {
        // Admin 검증
        User user = findUserByUserUuid(userAdminUuid);
        user.validateAdminRole();

        // 딜리버리 조회
        Delivery delivery = findDeliveryByDeliveryUuid(deliveryUuid);


        // 유저 조회
        Customer customer = customerPersistencePort.findByCustomerUuid(delivery.getCustomerUuid())
                .orElseThrow(() -> new CustomerNotFoundException("유저를 찾을 수 없습니다."));

        // 아이템 조회
        Page<OrderItem> orderItems = orderItemPersistencePort.findAllByDeliveryUuid(deliveryUuid, pageable);

        Page<OrderItemResponseDto> itemPage = orderItems.map(orderItemMapper::toRequestDeliveryResponse);


        return deliveryMapper.toRequestDeliveryOrderItemResponse(delivery, customer, itemPage);
    }

    @Override
    public DeliveryResponseDto getCancelReason(UUID userAdminUuid, UUID deliveryUuid) {
        // Admin 검증
        User user = findUserByUserUuid(userAdminUuid);
        user.validateAdminRole();

        // 딜리버리 조회 및 검증
        Delivery delivery = findDeliveryByDeliveryUuid(deliveryUuid);

        if (delivery.getDeliveryStatus() != DeliveryStatus.CANCELLED){
            throw new DeliveryInvalidException("취소 상태에서만 조회가 가능합니다.");
        }

        return deliveryMapper.toCancelResponse(delivery);

    }


    /**
     * 유저가 배송정보를 확인하는 프로세스
     *
     * @param userUuid
     * @param deliveryUuid
     * @return 배송정보 응답 DTO
     */
    @Override
    public DeliveryResponseDto getDeliveryInfo(UUID userUuid, UUID deliveryUuid) {
        log.info("Searching delivery for delivery uuid: {} and delivery uuid: {}", userUuid, deliveryUuid);
        Customer customer = findCustomerByUserUuid(userUuid);
        Delivery delivery = findDeliveryByCustomerUuidAndDeliveryUuid(deliveryUuid, customer);

        return deliveryMapper.toResponse(delivery);
    }


    /**
     * 유저가 상태에 따른 결제정보 확인
     *
     * @param userUuid
     * @param deliveryStatus
     * @param pageable
     * @return
     */
    @Override
    public Page<DeliveryResponseDto> getDeliveriesByStatus(UUID userUuid, DeliveryStatus deliveryStatus, Pageable pageable) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Page<Delivery> deliveries = deliveryPersistencePort.findByCustomerUuidByStatus(customer.getCustomerUuid(), deliveryStatus, pageable);
        return deliveries.map(deliveryMapper::searchResponse);
    }

    @Override
    public DeliveryCountResponseDto getDeliveryDashboardCount(UUID userUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);

        int shippedCount = deliveryPersistencePort.findByDeliveryStatusCount(customer.getCustomerUuid(), DeliveryStatus.SHIPPED);
        int deliveredCount = deliveryPersistencePort.findByDeliveryStatusCount(customer.getCustomerUuid(), DeliveryStatus.DELIVERED);

        return deliveryMapper.deliveryCount(shippedCount, deliveredCount);
    }

    @Override
    public int countDeliveryByStatus(UUID userUuid, DeliveryStatus status){
        Customer customer = findCustomerByUserUuid(userUuid);
        return deliveryPersistencePort.findByDeliveryStatusCount(customer.getCustomerUuid(), status);
    }


    // -- 업데이트

    @Override
    @Transactional
    public DeliveryResponseDto restoreDelivery(UUID userAdminUuid, UUID deliveryUuid) {
        // 관리자 검증 및 조회
        User user = findUserByUserUuid(userAdminUuid);
        user.validateAdminRole();

        Delivery delivery = findDeliveryByDeliveryUuid(deliveryUuid);

        // 보류로 상태변경
        delivery.changePending();

        //저장
        Delivery savedDelivery = deliveryPersistencePort.saveDelivery(delivery);

        return deliveryMapper.toDeliveryUuidResponse(savedDelivery);
    }


    @Override
    @Transactional
    public DeliveryResponseDto cancelDelivery(UUID userAdminUuid, UUID deliveryUuid, DeliveryCancelRequestDto requestDto){

        // 관리자 검증 및 조회
        User user = findUserByUserUuid(userAdminUuid);
        user.validateAdminRole();

        Delivery delivery = findDeliveryByDeliveryUuid(deliveryUuid);

        // 상태변경 및 이유 작성
        delivery.changeCancel(requestDto.getCancelReason());

        Delivery savedDelivery = deliveryPersistencePort.saveDelivery(delivery);

        // 배송과 연관된 박스, 박스에 속한 아이템 삭제 이벤트 발행
        eventPublisher.publishEvent(new DeliveryCancelledEvent(deliveryUuid));

        //저장
        return deliveryMapper.toDeliveryUuidResponse(savedDelivery);

    }


    @Override
    @Transactional
    public void updateTotalShipmentFee(UUID deliveryUuid, BigDecimal totalShipmentFee) {

        //조회
        Delivery delivery = findDeliveryByDeliveryUuid(deliveryUuid);

        //변경 및 검증
        delivery.changeTotalShipmentFee(totalShipmentFee);


        deliveryPersistencePort.saveDelivery(delivery);
    }

    @Override
    @Transactional
    public void shippedDelivery(UUID deliveryUuid) {

        // 박스 전체조회
        List<ShipmentEntity> shipments = shipmentEntityRepository.findAllByDeliveryUuid(deliveryUuid);

        // 박스에 송장번호 등록이 다되어 있는지 검증
        boolean isAllTracked = shipments.stream()
                .allMatch(s -> s.getTrackingNo() != null && !s.getTrackingNo().isEmpty());

        Delivery delivery = findDeliveryByDeliveryUuid(deliveryUuid);

        if (isAllTracked) {
            delivery.changeShipped();
        } else {
            log.info("아직 송장이 입력되지 않은 박스가 있어 배송시작을 보류 합니다!. DeliveryUuid: {}", delivery.getDeliveryUuid());
        }
        deliveryPersistencePort.saveDelivery(delivery);
    }



    /**
     * 배송 준비중에서 물품이 배송업체로 인도되기 전에 구매자가 급히 주소변경 등 배송정보를 변경요청사항이 있으면 관리자가 수동으로 수정해는 프로세스
     * 기본적으로 유저가 수정은 불가능하다.
     *
     * @param userUuid
     * @param deliveryUuid
     * @param updateDto
     * @return
     */
    @Override
    @Transactional
    public DeliveryResponseDto updateDeliveryWithValidate(UUID userUuid, UUID deliveryUuid, DeliveryUpdateDto updateDto) {

        try {
            log.info("Updating delivery for delivery uuid:{}", deliveryUuid);
            Customer customer = findCustomerByUserUuid(userUuid);
            User user = findUserByUserUuid(userUuid);
            user.validateAdminRole();

            Delivery delivery = findDeliveryByCustomerUuidAndDeliveryUuid(deliveryUuid, customer);
            delivery.updateDelivery(updateDto);

            log.info("Updated delivery for delivery uuid:{}", delivery.getDeliveryUuid());
            return deliveryMapper.toResponse(delivery);
        } catch (Exception e) {
            log.error("Failed to update delivery for user uuid: {}", userUuid);
            throw new DeliveryUpdateException("Failed to update DeliveryJpaEntity", e);
        }
    }

    @Override
    @Transactional
    public void completedPacking(UUID deliveryUuid) {

        // 조회
        Delivery delivery = findDeliveryByDeliveryUuid
                (deliveryUuid);

        // 상태변경
        delivery.completedPacking();

        deliveryPersistencePort.saveDelivery(delivery);
    }

    private Delivery findDeliveryByDeliveryUuid(UUID deliveryUuid) {
        return deliveryPersistencePort.findByDeliveryUuid(deliveryUuid)
                .orElseThrow(() -> new DeliveryNotFoundException("배송을 찾을 수 없습니다."));
    }

    @Override
    public void revertToPending(UUID deliveryUuid) {

        // 조회
        Delivery delivery = findDeliveryByDeliveryUuid(deliveryUuid);

        // 상태변경
        delivery.changePending();

        deliveryPersistencePort.saveDelivery(delivery);

    }


    // -- 삭제

    /**
     * 관리자의 배송 삭제 프로세스
     *
     * @param userUuid
     * @param deliveryUuid
     */
    @Override
    @Transactional
    public void deleteDelivery(UUID userUuid, UUID deliveryUuid) {
        log.info("Deleting delivery for delivery uuid:{}", deliveryUuid);
        Customer customer = findCustomerByUserUuid(userUuid);
        User user = userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for userUuid: %s", userUuid)));
        user.validateAdminRole();

        Delivery delivery = findDeliveryByCustomerUuidAndDeliveryUuid(deliveryUuid, customer);

        deliveryPersistencePort.deleteDelivery(delivery);
    }



    //helper method
    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for userUuid: %s", userUuid)));
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", userUuid)));
    }

    private Delivery findDeliveryByCustomerUuidAndDeliveryUuid(UUID deliveryUuid, Customer customer) {
        return deliveryPersistencePort.findByCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(), deliveryUuid)
                .orElseThrow(() -> new DeliveryNotFoundException(String.format("Delivery not found for customerUuid: %s and deliveryUuid: %s", customer.getCustomerUuid(), deliveryUuid)));
    }
}
