package com.kijinkai.domain.shipment.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.application.in.UpdateDeliveryUseCase;
import com.kijinkai.domain.delivery.application.out.DeliveryPersistencePort;
import com.kijinkai.domain.delivery.domain.event.DeliveryCancelledEvent;
import com.kijinkai.domain.delivery.domain.exception.DeliveryNotFoundException;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemValidateException;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.shipment.dto.shipment.request.ShipmentTrackingRequestDto;
import com.kijinkai.domain.shipment.dto.shipment.request.ShipmentUpdateDto;
import com.kijinkai.domain.shipment.dto.shipment.response.ShipmentBoxResponseDto;
import com.kijinkai.domain.shipment.dto.shipmentBoxItem.ShipmentBoxItemResponseDto;
import com.kijinkai.domain.shipment.dto.shipment.request.ShipmentRequestDto;
import com.kijinkai.domain.shipment.dto.shipment.response.ShipmentResponseDto;
import com.kijinkai.domain.shipment.dto.shipmentBoxItem.StartShipmentRequestDto;
import com.kijinkai.domain.shipment.entity.ShipmentBoxItemEntity;
import com.kijinkai.domain.shipment.entity.ShipmentEntity;
import com.kijinkai.domain.shipment.entity.ShipmentStatus;
import com.kijinkai.domain.shipment.mapper.ShipmentBoxItemMapper;
import com.kijinkai.domain.shipment.mapper.ShipmentMapper;
import com.kijinkai.domain.shipment.repository.ShipmentBoxItemEntityRepository;
import com.kijinkai.domain.shipment.repository.ShipmentEntityRepository;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.util.BusinessCodeType;
import com.kijinkai.util.GenerateBusinessItemCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentEntityRepository shipmentEntityRepository;
    private final ShipmentBoxItemEntityRepository shipmentBoxItemEntityRepository;

    private final CustomerPersistencePort customerPersistencePort;
    private final UserPersistencePort userPersistencePort;

    private final UpdateOrderItemUseCase updateOrderItemUseCase;
    private final OrderItemPersistencePort orderItemPersistencePort;
    private final UpdateDeliveryUseCase updateDeliveryUseCase;
    private final ShipmentBoxItemMapper shipmentBoxItemMapper;
    private final ShipmentMapper shipmentMapper;
    private final DeliveryPersistencePort deliveryPersistencePort;


    //util
    private final GenerateBusinessItemCode generateBusinessItemCode;


    /**
     * 대기중 상품 포장 후 박스 생성
     *
     * @param userUuid
     * @param deliveryUuid
     * @param requestDto
     * @return 수정 -> 패킹하면 딜리버리 상태변경,  가지고 있는 상품 다 패킹해야함, 안하면 패킹 실패 처리
     */
    @Override
    @Transactional
    public List<UUID> createDeliveryBox(UUID userUuid, UUID deliveryUuid, ShipmentRequestDto requestDto) {

        // 접근자가 관리자인지 권한 검증
        User user = findByUserUuid(userUuid);
        user.validateAdminRole();

        // 주문 상품 조회 및 검증
        List<OrderItem> orderItems = orderItemPersistencePort.findAllByDeliveryUuid(deliveryUuid);

        // 주문상품이 없을 경우 체크
        if (orderItems.isEmpty()) {
            throw new OrderItemNotFoundException("주문 상품이 존재하지 않습니다.");
        }

        // 첫번째 주문상품 추출
        OrderItem orderItem = orderItems.get(0);

        Set<String> validItemCodes = orderItems.stream().map(OrderItem::getOrderItemCode).collect(Collectors.toSet());
        boolean isValid = requestDto.getOrderItemCodes().stream().anyMatch(validItemCodes::contains);
        if (!isValid) {
            throw new OrderItemValidateException("배송 요청 목록에 있는 상품과 요청된 상품이 일치하지 않습니다.");
        }

        // 상품별 수량 체크
        List<UUID> createdShipmentUuids = new ArrayList<>();

        for (ShipmentRequestDto.BoxInfo boxInfo : requestDto.getBoxes()) {

            ShipmentEntity shipment = ShipmentEntity.builder()
                    .shipmentUuid(UUID.randomUUID())
                    .deliveryUuid(deliveryUuid)
                    .customerUuid(orderItem.getCustomerUuid())
                    .boxCode(generateBusinessItemCode.generateBusinessCode(userUuid.toString(), BusinessCodeType.BOX))
                    .totalWeight(boxInfo.getWeight())
                    .shippingFee(boxInfo.getShippingFee())
                    .shipmentStatus(ShipmentStatus.PAYMENT_PENDING)
                    .build();

            ShipmentEntity savedShipment = shipmentEntityRepository.save(shipment);
            createdShipmentUuids.add(savedShipment.getShipmentUuid());

            // 박스 아이템 저장 박스
            List<ShipmentBoxItemEntity> boxItems = new ArrayList<>();
            List<String> currentBoxItemCodes = new ArrayList<>();

            Map<String, OrderItem> orderItemMap = orderItemPersistencePort.findAllByOrderItemCodeIn(requestDto.getOrderItemCodes())
                    .stream()
                    .collect(Collectors.toMap(
                            OrderItem::getOrderItemCode,
                            o -> o,
                            (e, r) -> e
                    ));


            for (ShipmentRequestDto.OrderItemInfo itemInfo : boxInfo.getOrderItemInfos()) {

                if (itemInfo.getQuantity() < orderItem.getQuantity()) {
                    throw new OrderItemValidateException("주문된 수량 이상 등록이 불가능합니다.");
                }

                OrderItem oi = orderItemMap.get(itemInfo.getOrderItemCode());

                ShipmentBoxItemEntity boxItem = ShipmentBoxItemEntity.builder()
                        .shipmentEntity(savedShipment)
                        .oderItemUuid(oi.getOrderItemUuid())
                        .orderItemCode(oi.getOrderItemCode())
                        .quantity(itemInfo.getQuantity())
                        .build();

                // 추가
                boxItems.add(boxItem);
                currentBoxItemCodes.add(itemInfo.getOrderItemCode());
            }


            // 한번에 DB에 저장
            shipmentBoxItemEntityRepository.saveAll(boxItems);
            updateOrderItemUseCase.assignToShipment(currentBoxItemCodes, savedShipment.getShipmentUuid());

        }

        // 딜리버리 상태 변경 및 검증
        updateDeliveryUseCase.completedPacking(deliveryUuid);

        return createdShipmentUuids;
    }


    // ---업데이트.

    /*
    송장번호 입력 후 배송시작
     */
    @Override
    @Transactional
    public ShipmentResponseDto addTrackingNo(UUID userAdminUuid, UUID shipmentUuid, ShipmentTrackingRequestDto requestDto) {

        //조회 및 검증
        User user = findByUserUuid(userAdminUuid);
        user.validateAdminRole();

        ShipmentEntity shipment = findShipmentByShipmentUuid(shipmentUuid);

        //트랙킹넘버 및 배송시작 상태변경
        shipment.addTrackingNoAndChangeShipped(requestDto.getTackingNo());

        ShipmentEntity savedShipment = shipmentEntityRepository.save(shipment);

        //배송시작으로상태변경 -- 지불된것도 반대로 상태변경
        updateDeliveryUseCase.shippedDelivery(savedShipment.getDeliveryUuid());

        return shipmentMapper.toShipmentUuidResponseDto(savedShipment);
    }


    @Override
    @Transactional
    public void paidShipment(UUID deliveryUuid) {

        List<ShipmentEntity> shipments = shipmentEntityRepository.findAllByDeliveryUuid(deliveryUuid);

        shipments.forEach(ShipmentEntity::updatePaid);

        shipmentEntityRepository.saveAll(shipments);

    }


    @Override
    @Transactional
    public ShipmentResponseDto updatePackedShipment(UUID userAdminUuid, UUID shipmentUuid, ShipmentUpdateDto updateDto) {

        //조회 및 검증
        User user = findByUserUuid(userAdminUuid);
        user.validateAdminRole();

        ShipmentEntity shipment = findShipmentByShipmentUuid(shipmentUuid);

        shipment.updatePackedShipment(updateDto);

        ShipmentEntity savedShipment = shipmentEntityRepository.save(shipment);

        recalculateDeliveryTotalFee(savedShipment.getDeliveryUuid());

        return shipmentMapper.toShipmentUuidResponseDto(savedShipment);
    }

    private ShipmentEntity findShipmentByShipmentUuid(UUID shipmentUuid) {
        return shipmentEntityRepository.findByShipmentUuid(shipmentUuid)
                .orElseThrow(() -> new IllegalArgumentException("박스가 존재하지 않습니다."));
    }

    @Override
    @Transactional
    public String delivered(UUID userUuid, String boxCode, ShipmentStatus status) {

        // 구매자 조회
        Customer customer = findCustomerByUserUuid(userUuid);

        // 상태에 따른 배송조회
        ShipmentEntity shipment = shipmentEntityRepository.findByCustomerUuidAndBoxCodeAndShipmentStatus(customer.getCustomerUuid(), boxCode, status)
                .orElseThrow(() -> new IllegalArgumentException("not found shipment"));

        // 상태변경
        shipment.delivered();

        //저장 - 변경감지

        // 상품들 상태변경
        updateOrderItemUseCase.delivered(shipment.getShipmentUuid());

        return shipment.getBoxCode();
    }


    // 업데이트.

    @Override
    @Transactional
    public void registerOrderPaymentToShipment(List<ShipmentEntity> shipmentEntities, UUID orderPaymentUuid) {

        for (ShipmentEntity shipmentEntity : shipmentEntities) {
            shipmentEntity.completePayment(orderPaymentUuid);
        }

        shipmentEntityRepository.saveAll(shipmentEntities);
    }


    // 조회.

    @Override
    public Page<ShipmentResponseDto> getShipmentsByStatus(UUID userUuid, ShipmentStatus shipmentStatus, Pageable pageable) {

        Customer customer = findCustomerByUserUuid(userUuid);

        Page<ShipmentEntity> shipmentsByStatus = shipmentEntityRepository.findAllByCustomerUuidAndShipmentStatus(customer.getCustomerUuid(), shipmentStatus, pageable);


        return shipmentsByStatus.map(
                shipmentEntity ->
                        ShipmentResponseDto.builder()
                                .boxCodes(shipmentEntity.getBoxCode())
                                .shipmentFee(shipmentEntity.getShippingFee())
                                .weight(shipmentEntity.getTotalWeight())
                                .trackingNo(shipmentEntity.getTrackingNo())
                                .build()
        );
    }

    @Override
    public List<ShipmentBoxItemResponseDto> getBoxItems(UUID userUuid, String boxCode) {

        // 구매자 조회
        Customer customer = findCustomerByUserUuid(userUuid);

        //shipmentBoxItem 조회
        List<ShipmentBoxItemEntity> shipmentBoxItems = shipmentBoxItemEntityRepository.findAllByShipmentEntityCustomerUuidAndShipmentEntityBoxCode(customer.getCustomerUuid(), boxCode);
        if (shipmentBoxItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> orderItemCodes = shipmentBoxItems.stream().map(ShipmentBoxItemEntity::getOrderItemCode).toList();

        List<OrderItem> orderItems = orderItemPersistencePort.findAllByCustomerUuidAndOrderItemCodeIn(customer.getCustomerUuid(), orderItemCodes);

        Map<String, OrderItem> orderItemMap = orderItems.stream()
                .collect(Collectors.toMap(OrderItem::getOrderItemCode, item -> item));

        return shipmentBoxItems.stream()
                .map(shipmentBoxItem -> {
                    OrderItem orderItem = orderItemMap.get(shipmentBoxItem.getOrderItemCode());

                    // 데이터 무결성 체크 (혹시 DB에 없는 경우)
                    if (orderItem == null) {
                        throw new OrderItemNotFoundException("Not found order item: " + shipmentBoxItem.getOrderItemCode());
                    }

                    return ShipmentBoxItemResponseDto.builder()
                            .productLink(orderItem.getProductLink())
                            .orderItemCode(orderItem.getOrderItemCode())
                            .price(orderItem.getPriceOriginal().multiply(BigDecimal.valueOf(shipmentBoxItem.getQuantity())))
                            .quantity(shipmentBoxItem.getQuantity())
                            .build();
                })
                .toList();
    }


    /*
    포장 완료된 상품 리스트
     */
    @Override
    public ShipmentResponseDto getPackagesByAdmin(UUID userAdminUuid, UUID deliveryUuid, Pageable pageable) {

        // 관리자 체크 및 기본 조회
        User user = findByUserUuid(userAdminUuid);
        user.validateAdminRole();

        Delivery delivery = deliveryPersistencePort.findByDeliveryUuid(deliveryUuid)
                .orElseThrow(() -> new DeliveryNotFoundException("배송을 찾을 수 없습니다."));

        Customer customer = customerPersistencePort.findByCustomerUuid(delivery.getCustomerUuid())
                .orElseThrow(() -> new CustomerNotFoundException("유저를 찾을 수 없습니다."));


        // 해당 배송에 속한 shipment 조회 및 체크
        Page<ShipmentEntity> shipments = shipmentEntityRepository.findAllByDeliveryUuid(deliveryUuid, pageable);

        if (shipments.isEmpty()) {
            return shipmentMapper.toShipmentResponseDto(delivery, customer, Page.empty(pageable));
        }

        // shipmeIds를 추출
        List<Long> shipmentIds = shipments.stream()
                .map(ShipmentEntity::getShipmentId)
                .distinct()
                .toList();

        // BoxItem 조회 및 그룹핑  -> page를 쓰면 누락되니까 list로 가져와야함
        List<ShipmentBoxItemEntity> allBoxItems = shipmentBoxItemEntityRepository.findAllByShipmentEntityShipmentIdIn(shipmentIds);

        Map<Long, List<ShipmentBoxItemEntity>> boxItemsGroupedByShipmentId = allBoxItems.stream()
                .collect(Collectors.groupingBy(item -> item.getShipmentEntity().getShipmentId()));

        List<UUID> orderItemUuidsByBox = allBoxItems.stream()
                .map(ShipmentBoxItemEntity::getOderItemUuid)
                .distinct()
                .toList();

        Map<UUID, OrderItem> orderItemMap = orderItemPersistencePort.findAllByOrderItemUuidIn(orderItemUuidsByBox)
                .stream()
                .collect(Collectors.toMap(
                        OrderItem::getOrderItemUuid,
                        o -> o,
                        (e, r) -> e
                ));

        Page<ShipmentBoxResponseDto> shipmentList = shipments.map(shipment ->
                {
                    List<ShipmentBoxItemEntity> items = boxItemsGroupedByShipmentId.getOrDefault(shipment.getShipmentId(), Collections.emptyList());

                    List<ShipmentBoxItemResponseDto> shipmentBoxItemResponseDtos = items.stream()
                            .map(shipmentBoxItem -> {
                                OrderItem orderItem = orderItemMap.get(shipmentBoxItem.getOderItemUuid());
                                return shipmentBoxItemMapper.toPackedResponse(shipmentBoxItem, orderItem);
                            }).toList();


                    return shipmentMapper.toShipmentListResponseDto(shipment, shipmentBoxItemResponseDtos);
                }
        );


        return shipmentMapper.toShipmentResponseDto(delivery, customer, shipmentList);
    }


    // 패킹 된 페이지에서 업데이트를 시켜줌 - 수령인, 배송방식, 연락처 통관부호, 주소 , 박스에 들어있는 상품이랑 갯수


    /**
     * 유저의 상태별 카운트 조회
     *
     * @param userUuid
     * @param status
     * @return
     */
    @Override
    public int countShipmentByStatus(UUID userUuid, ShipmentStatus status) {
        Customer customer = findCustomerByUserUuid(userUuid);
        return shipmentEntityRepository.findShipmentCountByStatus(customer.getCustomerUuid(), status);
    }

    // -- 삭제
    @Override
    @Transactional
    public void cancelPacked(UUID userAdminUuid, UUID deliveryUuid) {

        // 관리자 체크 및 기본 조회
        User user = findByUserUuid(userAdminUuid);
        user.validateAdminRole();

        // 박스, 박스 아이템 삭제
        deleteAllByDelivery(deliveryUuid);

        // 딜리버리 다시 보류  상태 변경
        updateDeliveryUseCase.revertToPending(deliveryUuid);
    }

    @Override
    @Transactional
    public void deleteAllByDelivery(UUID deliveryUuid) {
        // 딜리버리Uuid로  박스들 리스트 조회
        List<ShipmentEntity> shipments = shipmentEntityRepository.findAllByDeliveryUuid(deliveryUuid);

        if (shipments.isEmpty()) {
            return;
        }

        List<Long> shipmentIds = shipments.stream()
                .map(ShipmentEntity::getShipmentId)
                .toList();

        // 박스 들어간 아이템 삭제
        shipmentBoxItemEntityRepository.deleteByShipmentEntityShipmentIdIn(shipmentIds);

        // DB반영
        shipmentBoxItemEntityRepository.flush();

        // 박스 삭제
        shipmentEntityRepository.deleteAllInBatch(shipments);

    }


    // -- helper. --
    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid: %s", userUuid)));
    }

    private void recalculateDeliveryTotalFee(UUID deliveryUuid) {

        List<ShipmentEntity> shipments = shipmentEntityRepository.findAllByDeliveryUuid(deliveryUuid);

        BigDecimal total = shipments.stream()
                .map(ShipmentEntity::getShippingFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        updateDeliveryUseCase.updateTotalShipmentFee(deliveryUuid, total);
    }

    private User findByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for userUuid: %s", userUuid)));
    }


    // event
    @EventListener
    @Transactional
    public void handleDeliveryCanceled(DeliveryCancelledEvent event){
        UUID deliveryUuid = event.deliveryUuid();

        // 삭제호출
        this.deleteAllByDelivery(deliveryUuid);
    }


}
