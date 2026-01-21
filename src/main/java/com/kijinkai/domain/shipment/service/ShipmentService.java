package com.kijinkai.domain.shipment.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.orderitem.application.port.in.UpdateOrderItemUseCase;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemValidateException;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.shipment.dto.ShipmentBoxItemResponseDto;
import com.kijinkai.domain.shipment.dto.ShipmentRequestDto;
import com.kijinkai.domain.shipment.dto.ShipmentResponseDto;
import com.kijinkai.domain.shipment.dto.StartShipmentRequestDto;
import com.kijinkai.domain.shipment.entity.ShipmentBoxItemEntity;
import com.kijinkai.domain.shipment.entity.ShipmentEntity;
import com.kijinkai.domain.shipment.entity.ShipmentStatus;
import com.kijinkai.domain.shipment.repository.ShipmentBoxItemEntityRepository;
import com.kijinkai.domain.shipment.repository.ShipmentEntityRepository;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.util.BusinessCodeType;
import com.kijinkai.util.GenerateBusinessItemCode;
import lombok.RequiredArgsConstructor;
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
public class ShipmentService {

    private final ShipmentEntityRepository shipmentEntityRepository;
    private final ShipmentBoxItemEntityRepository shipmentBoxItemEntityRepository;

    private final CustomerPersistencePort customerPersistencePort;
    private final UserPersistencePort userPersistencePort;

    private final UpdateOrderItemUseCase updateOrderItemUseCase;
    private final OrderItemPersistencePort orderItemPersistencePort;

    //util
    private final GenerateBusinessItemCode generateBusinessItemCode;


    /**
     * 대기중 상품 포장 후 박스 생성
     * @param userUuid
     * @param deliveryUuid
     * @param requestDto
     * @return
     */
    @Transactional
    public List<UUID> createDeliveryBox(UUID userUuid, UUID deliveryUuid, ShipmentRequestDto requestDto) {


        // 접근자가 관리자인지 권한 검증
        User user = findByUserUuid(userUuid);
        user.validateAdminRole();

        // 주문 상품 조회 및 검증
        List<OrderItem> orderItems = orderItemPersistencePort.findAllByDeliveryUuid(deliveryUuid);

        if (orderItems.isEmpty()) {
            throw new OrderItemNotFoundException("주문 상품이 존재하지 않습니다.");
        }

        OrderItem orderItem = orderItems.get(0);

        Set<String> validItemCodes = orderItems.stream().map(OrderItem::getOrderItemCode).collect(Collectors.toSet());
        boolean isValid = requestDto.getOrderItemCodes().stream().anyMatch(validItemCodes::contains);
        if (!isValid){
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

            for (ShipmentRequestDto.OrderItemInfo itemInfo : boxInfo.getOrderItemInfos()) {

                if (itemInfo.getQuantity() < orderItem.getQuantity()){
                    throw new OrderItemValidateException("주문된 수량 이상 등록이 불가능합니다.");
                }

                ShipmentBoxItemEntity boxItem = ShipmentBoxItemEntity.builder()
                        .shipmentEntity(savedShipment)
                        .orderItemCode(itemInfo.getOrderItemCode())
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

        return createdShipmentUuids;
    }


    // ---업데이트.


    @Transactional
    public String delivered(UUID userUuid, String boxCode, ShipmentStatus status){

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



    @Transactional
    public String registerTrackingNumber(UUID userUuid, String boxCode, StartShipmentRequestDto requestDto){

        //관리자 체크
        User user = findByUserUuid(userUuid);
        user.validateAdminRole();

        //shipment 조회
        ShipmentEntity shipment = shipmentEntityRepository.findByBoxCodeAndShipmentStatus(boxCode, ShipmentStatus.PREPARING)
                .orElseThrow(() -> new IllegalArgumentException("Not found Box code"));

        // 상태 변경 및 trackingNo 등록
        shipment.startShipment(requestDto);

        // 저장 - 변경감지

        //상품들 상태 변경 및 저장
        updateOrderItemUseCase.startDelivery(shipment.getShipmentUuid());

        return shipment.getBoxCode();
    }
    // 업데이트.

    @Transactional
    public void registerOrderPaymentToShipment(List<ShipmentEntity> shipmentEntities, UUID orderPaymentUuid){

        for (ShipmentEntity shipmentEntity : shipmentEntities) {
            shipmentEntity.completePayment(orderPaymentUuid);
        }

        shipmentEntityRepository.saveAll(shipmentEntities);
    }




    // 조회.


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
                            .url(orderItem.getProductLink())
                            .orderItemCode(orderItem.getOrderItemCode())
                            .price(orderItem.getPriceOriginal().multiply(BigDecimal.valueOf(shipmentBoxItem.getQuantity())))
                            .quantity(shipmentBoxItem.getQuantity())
                            .build();
                })
                .toList();
    }




    /**
     * 유저의 상태별 카운트 조회
     * @param userUuid
     * @param status
     * @return
     */
    public int countShipmentByStatus(UUID userUuid, ShipmentStatus status){
        Customer customer = findCustomerByUserUuid(userUuid);
        return shipmentEntityRepository.findShipmentCountByStatus(customer.getCustomerUuid(), status);
    }

    // -- helper. --
    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid: %s", userUuid)));
    }

    private User findByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for userUuid: %s", userUuid)));
    }


}
