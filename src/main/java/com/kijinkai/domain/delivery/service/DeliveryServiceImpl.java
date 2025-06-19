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
import com.kijinkai.domain.delivery.exception.DeliveryNotFoundException;
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

    @Override
    public DeliveryResponseDto createDeliveryWithValidate(String userUuid, String orderUuid, DeliveryRequestDto requestDto) {

        log.info("Creating delivery for user uuid:{}", userUuid);

        Order order = findOrderByOrderUuid(orderUuid);
        orderValidator.requirePaidStatusForConfirmation(order);

        Address address = findAddressByCustomerUuid(order);
        Delivery delivery = factory.createDelivery(order, address, requestDto);

        Delivery savedDelivery = deliveryRepository.save(delivery);
        order.updateOrderState(OrderStatus.PREPARE_DELIVERY);

        log.info("Created delivery for delivery uuid:{}", savedDelivery.getDeliveryUuid());
        return deliveryMapper.toResponse(savedDelivery);
    }

    @Override // 배송시작 버튼
    public DeliveryResponseDto deliveryShipped(String userUuid, String deliveryUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Delivery delivery = findDeliveryByCustomerAndDeliveryUuid(customer, deliveryUuid);

        userValidator.requireAdminRole(customer.getUser());
        deliveryValidator.requirePendingStatus(delivery);

        Order order = delivery.getOrder();
        order.updateOrderState(OrderStatus.SHIPPING);
        delivery.updateDeliveryStatus(DeliveryStatus.SHIPPED);

        return deliveryMapper.toResponse(delivery);
    }

    private void updateDelivery(Delivery delivery, DeliveryUpdateDto updateDto) {

        delivery.updateDelivery(
                updateDto.getRecipientName(),
                updateDto.getRecipientPhoneNumber(),
                updateDto.getCountry(),
                updateDto.getZipcode(),
                updateDto.getState(),
                updateDto.getCity(),
                updateDto.getStreet(),
                updateDto.getCarrier(),
                updateDto.getTrackingNumber(),
                updateDto.getDeliveryFee()
        );
    }

    @Override // admin
    public DeliveryResponseDto updateDeliveryWithValidate(String userUuid, String deliveryUuid, DeliveryUpdateDto updateDto) {

        log.info("Updating delivery for delivery uuid:{}", deliveryUuid);
        Customer customer = findCustomerByUserUuid(userUuid);
        userValidator.requireAdminRole(customer.getUser());

        Delivery delivery = findDeliveryByCustomerAndDeliveryUuid(customer, deliveryUuid);
        orderValidator.requirePaidStatusForConfirmation(delivery.getOrder());

        updateDelivery(delivery, updateDto);

        log.info("Updated delivery for delivery uuid:{}", delivery.getDeliveryUuid());
        return deliveryMapper.toResponse(delivery);
    }

    @Override
    public void deleteDelivery(String userUuid, String deliveryUuid) {
        log.info("Deleting delivery for delivery uuid:{}", deliveryUuid);
        Customer customer = findCustomerByUserUuid(userUuid);
        Delivery delivery = findDeliveryByCustomerAndDeliveryUuid(customer, deliveryUuid);

        deliveryRepository.delete(delivery);
    }

    @Override
    public DeliveryResponseDto getDeliveryInfo(String userUuid, String deliveryUuid) {
        log.info("Searching delivery for delivery uuid:{}", userUuid);
        Customer customer = findCustomerByUserUuid(userUuid);
        Delivery delivery = findDeliveryByCustomerAndDeliveryUuid(customer, deliveryUuid);

        log.info("Searching delivery for delivery uuid:{}", delivery.getDeliveryUuid());
        return deliveryMapper.toResponse(delivery);
    }

    private Customer findCustomerByUserUuid(String userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException("userUuid: customer not found"));
    }

    private Delivery findDeliveryByCustomerAndDeliveryUuid(Customer customer, String deliveryUuid) {
        return deliveryRepository.findByCustomerCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(), deliveryUuid)
                .orElseThrow(() -> new DeliveryNotFoundException("CustomerUuidAndDeliveryUuid: delivery not found"));
    }

    private Address findAddressByCustomerUuid(Order order) {
        return addressRepository.findByCustomerCustomerUuid(order.getCustomer().getCustomerUuid())
                .orElseThrow(() -> new CustomerNotFoundException("CustomerUuid: Address not found"));
    }

    private Order findOrderByOrderUuid(String orderUuid) {
        return orderRepository.findByOrderUuid(orderUuid)
                .orElseThrow(() -> new OrderNotFoundException("OrderUuid: Order not found"));
    }
}
