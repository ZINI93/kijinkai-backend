package com.kijinkai.domain.delivery.service;


import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.address.repository.AddressRepository;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.entity.Delivery;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import com.kijinkai.domain.delivery.exception.DeliveryCreationException;
import com.kijinkai.domain.delivery.exception.DeliveryNotFoundException;
import com.kijinkai.domain.delivery.exception.DeliveryUpdateException;
import com.kijinkai.domain.delivery.factory.DeliveryFactory;
import com.kijinkai.domain.delivery.mapper.DeliveryMapper;
import com.kijinkai.domain.delivery.repository.DeliveryRepository;
import com.kijinkai.domain.delivery.validator.DeliveryValidator;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.order.entity.OrderStatus;
import com.kijinkai.domain.order.exception.OrderNotFoundException;
import com.kijinkai.domain.order.repository.OrderRepository;
import com.kijinkai.domain.order.validator.OrderValidator;
import com.kijinkai.domain.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final CustomerRepository customerRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;

    private final DeliveryFactory factory;
    private final DeliveryMapper deliveryMapper;

    private final DeliveryValidator deliveryValidator;
    private final UserValidator userValidator;
    private final OrderValidator orderValidator;


    /**
     * 결제가 완료된 견적서에 상품을 구매 후 배송을 준비하는 프로세스
     *
     * @param userUuid
     * @param orderUuid
     * @param requestDto
     * @return 주문생성 응답 DTO
     */
    @Override @Transactional
    public DeliveryResponseDto createDeliveryWithValidate(UUID userUuid, UUID orderUuid, DeliveryRequestDto requestDto) {

        log.info("Creating delivery for user uuid: {}", userUuid);

        try {
            Order order = findOrderByOrderUuid(orderUuid);
            orderValidator.requirePaidStatusForConfirmation(order);

            Address address = findAddressByCustomerOrder(order);
            Delivery delivery = factory.createDelivery(order, address, requestDto);

            Delivery savedDelivery = deliveryRepository.save(delivery);
            order.prepareDeliveryOrder();

            log.info("Created delivery for delivery uuid:{}", savedDelivery.getDeliveryUuid());
            return deliveryMapper.toResponse(savedDelivery);
        } catch (Exception e) {
            log.error("Failed to create delivery for user uuid: {}", userUuid);
            throw new DeliveryCreationException("Failed to create delivery", e);
        }
    }

    /**
     * 견적서에 적힌 물품 구매가 완된 후 해당 나라로 배송을 시작
     *
     * @param userUuid
     * @param deliveryUuid
     * @return 배송시작 응답 DTO
     */
    @Override @Transactional // 배송시작 버튼
    public DeliveryResponseDto deliveryShipped(UUID userUuid, UUID deliveryUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Delivery delivery = findDeliveryByCustomerAndDeliveryUuid(customer, deliveryUuid);

        userValidator.requireAdminRole(customer.getUser());
        deliveryValidator.requirePendingStatus(delivery);


        Order order = delivery.getOrder();
        order.updateOrderStatus(OrderStatus.SHIPPING);
        delivery.updateDeliveryStatus(DeliveryStatus.SHIPPED);

        return deliveryMapper.toResponse(delivery);
    }


    /**
     * 배송 준비중에서 물품이 배송업체로 인도되기 전에 구매자가 급히 주소변경 등 배송정보를 변경요청사항이 있으면 관리자가 수동으로 수정해는 프로세스
     * 기본적으로 유저가 수정은 불가능하다.
     * @param userUuid
     * @param deliveryUuid
     * @param updateDto
     * @return
     */
    @Override @Transactional // admin
    public DeliveryResponseDto updateDeliveryWithValidate(UUID userUuid, UUID deliveryUuid, DeliveryUpdateDto updateDto) {

        try {
            log.info("Updating delivery for delivery uuid:{}", deliveryUuid);
            Customer customer = findCustomerByUserUuid(userUuid);
            userValidator.requireAdminRole(customer.getUser());

            Delivery delivery = findDeliveryByCustomerAndDeliveryUuid(customer, deliveryUuid);
            orderValidator.requirePaidStatusForConfirmation(delivery.getOrder());

            delivery.updateDelivery(updateDto);

            log.info("Updated delivery for delivery uuid:{}", delivery.getDeliveryUuid());
            return deliveryMapper.toResponse(delivery);
        } catch (Exception e) {
            log.error("Failed to update delivery for user uuid: {}", userUuid);
            throw new DeliveryUpdateException("Failed to update Delivery", e);
        }
    }

    /**
     * 관리자의 배송 삭제 프로세스
     * @param userUuid
     * @param deliveryUuid
     */

    @Override @Transactional
    public void deleteDelivery(UUID userUuid, UUID deliveryUuid) {

        log.info("Deleting delivery for delivery uuid:{}", deliveryUuid);
        Customer customer = findCustomerByUserUuid(userUuid);
        userValidator.requireAdminRole(customer.getUser());
        Delivery delivery = findDeliveryByCustomerAndDeliveryUuid(customer, deliveryUuid);

        deliveryRepository.delete(delivery);
    }

    /**
     * 유저가 배송정보를 확인하는 프로세스
     * @param userUuid
     * @param deliveryUuid
     * @return 배송정보 응답 DTO
     */

    @Override
    public DeliveryResponseDto getDeliveryInfo(UUID userUuid, UUID deliveryUuid) {
        log.info("Searching delivery for delivery uuid: {} and delivery uuid: {}", userUuid, deliveryUuid);
        Customer customer = findCustomerByUserUuid(userUuid);
        Delivery delivery = findDeliveryByCustomerAndDeliveryUuid(customer, deliveryUuid);

        return deliveryMapper.toResponse(delivery);
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException("userUuid: customer not found"));
    }

    private Delivery findDeliveryByCustomerAndDeliveryUuid(Customer customer, UUID deliveryUuid) {
        return deliveryRepository.findByCustomerCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(), deliveryUuid)
                .orElseThrow(() -> new DeliveryNotFoundException("CustomerUuidAndDeliveryUuid: delivery not found"));
    }

    private Address findAddressByCustomerOrder(Order order
    ) {
        return addressRepository.findByCustomerCustomerUuid(order.getCustomer().getCustomerUuid())
                .orElseThrow(() -> new CustomerNotFoundException("CustomerUuid: Address not found"));
    }

    private Order findOrderByOrderUuid(UUID orderUuid) {
        return orderRepository.findByOrderUuid(orderUuid)
                .orElseThrow(() -> new OrderNotFoundException("OrderUuid: Order not found"));
    }
}
