package com.kijinkai.domain.delivery.service;


import com.kijinkai.domain.address.entity.Address;
import com.kijinkai.domain.address.repository.AddressRepository;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.delivery.dto.DeliveryCountResponseDto;
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
import com.kijinkai.domain.order.validator.OrderValidator;
import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import com.kijinkai.domain.payment.domain.exception.OrderPaymentNotFoundException;
import com.kijinkai.domain.payment.domain.repository.OrderPaymentRepository;
import com.kijinkai.domain.user.adapter.out.persistence.entity.UserJpaEntity;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.adapter.out.persistence.repository.UserRepository;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
    private final OrderPaymentRepository orderPaymentRepository;


    private final DeliveryFactory factory;
    private final DeliveryMapper deliveryMapper;

    private final DeliveryValidator deliveryValidator;
    private final UserApplicationValidator userValidator;
    private final OrderValidator orderValidator;

    // 유저가 작성할 것인가?? // 관리자가 작성할 것인가??
    // 완료된 결제에 관리자가 작성하는게 좋을것 같은데, 주소 확인 표시
    // 한 결제에 여러가지 배송이 들어갈 수 있다.
    // orderPayment 배송을 list로 생성해야 한다.

    /**
     *
     * @param userUuid
     * @param orderPaymentUuid - 2차 결제 orderPayment
     * @param requestDto
     * @return
     */
    @Override @Transactional
    public DeliveryResponseDto createDeliveryWithValidate(UUID userUuid, UUID orderPaymentUuid, DeliveryRequestDto requestDto) {

        log.info("Creating delivery for user uuid: {}", userUuid);

        UserJpaEntity user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found exception for userUuid: %s", userUuid)));
        userValidator.requireJpaAdminRole(user);

        OrderPayment secondOrderPayment = orderPaymentRepository.findByPaymentUuid(orderPaymentUuid)
                .orElseThrow(() -> new OrderPaymentNotFoundException(String.format("Order payment not found exception for orderPaymentUuid: %s", orderPaymentUuid)));
        //orderpayment 상태 validator 필요함

        Customer customer = customerRepository.findByCustomerUuid(secondOrderPayment.getCustomerUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found exception for customer uuid: %s", secondOrderPayment.getCustomerUuid())));

        Address address = findAddressByCustomerOrder(customer.getCustomerUuid());

        try {
            Delivery delivery = factory.createDelivery(orderPaymentUuid, customer, address, secondOrderPayment.getPaymentAmount() , requestDto);
            Delivery savedDelivery = deliveryRepository.save(delivery);

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

        userValidator.requireJpaAdminRole(customer.getUser());
        deliveryValidator.requirePendingStatus(delivery);

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
            userValidator.requireJpaAdminRole(customer.getUser());

            Delivery delivery = findDeliveryByCustomerAndDeliveryUuid(customer, deliveryUuid);
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
        userValidator.requireJpaAdminRole(customer.getUser());
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

    /**
     * 유저가 상태에 따른 결제정보 확인
     * @param userUuid
     * @param deliveryStatus
     * @param pageable
     * @return
     */
    @Override
    public Page<DeliveryResponseDto> getDeliveriesByStatus(UUID userUuid, DeliveryStatus deliveryStatus, Pageable pageable) {
        Customer customer = findCustomerByUserUuid(userUuid);

        Page<Delivery> deliveries = deliveryRepository.findByCustomerUuidByStatus(customer.getCustomerUuid(), deliveryStatus, pageable);

        return deliveries.map(deliveryMapper::searchResponse);
    }

    @Override
    public DeliveryCountResponseDto getDeliveryDashboardCount(UUID userUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);

        int shippedCount = deliveryRepository.findByDeliveryStatusCount(customer.getCustomerUuid(), DeliveryStatus.SHIPPED);
        int deliveredCount = deliveryRepository.findByDeliveryStatusCount(customer.getCustomerUuid(), DeliveryStatus.DELIVERED);

        return deliveryMapper.deliveryCount(shippedCount, deliveredCount);
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException("userUuid: customer not found"));
    }

    private Delivery findDeliveryByCustomerAndDeliveryUuid(Customer customer, UUID deliveryUuid) {
        return deliveryRepository.findByCustomerCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(), deliveryUuid)
                .orElseThrow(() -> new DeliveryNotFoundException("CustomerUuidAndDeliveryUuid: delivery not found"));
    }

    private Address findAddressByCustomerOrder(UUID customerUuid) {
        return addressRepository.findByCustomerCustomerUuid(customerUuid)
                .orElseThrow(() -> new CustomerNotFoundException("CustomerUuid: Address not found"));
    }

}
