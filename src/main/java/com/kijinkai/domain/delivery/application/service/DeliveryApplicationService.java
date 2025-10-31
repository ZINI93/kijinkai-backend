package com.kijinkai.domain.delivery.application.service;

import com.kijinkai.domain.address.application.port.out.AddressPersistencePort;
import com.kijinkai.domain.address.domain.exception.AddressNotFoundException;
import com.kijinkai.domain.address.domain.model.Address;
import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.delivery.application.dto.DeliveryCountResponseDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.application.dto.DeliveryUpdateDto;
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
import com.kijinkai.domain.payment.adapter.out.persistence.entity.OrderPaymentJpaEntity;
import com.kijinkai.domain.payment.application.port.out.OrderPaymentPersistencePort;
import com.kijinkai.domain.payment.domain.exception.OrderPaymentNotFoundException;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
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
public class DeliveryApplicationService implements CreateDeliveryUseCase, DeleteDeliveryUseCase, GetDeliveryUseCase, UpdateDeliveryUseCase {


    private final DeliveryPersistencePort deliveryPersistencePort;

    private final DeliveryFactory factory;
    private final DeliveryMapper deliveryMapper;
    private final DeliveryValidator deliveryValidator;


    //외부
    private final CustomerPersistencePort customerPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final AddressPersistencePort addressPersistencePort;

    private final UserApplicationValidator userValidator;
    private final OrderValidator orderValidator;

    private final OrderPaymentPersistencePort orderPaymentPersistencePort;  // 수정필요


    /**
     *
     * @param userUuid
     * @param orderPaymentUuid - 2차 결제 orderPayment
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
            Delivery delivery = factory.createDelivery(orderPaymentUuid, customer.getCustomerUuid(), address, secondOrderPayment.getPaymentAmount(), requestDto);
            Delivery savedDelivery = deliveryPersistencePort.saveDelivery(delivery);

            return deliveryMapper.toResponse(savedDelivery);
        } catch (Exception e) {
            log.error("Failed to create delivery for user uuid: {}", userUuid);
            throw new DeliveryCreationException("Failed to create delivery", e);
        }
    }

    /**
     * 관리자의 배송 삭제 프로세스
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

    /**
     * 배송 준비중에서 물품이 배송업체로 인도되기 전에 구매자가 급히 주소변경 등 배송정보를 변경요청사항이 있으면 관리자가 수동으로 수정해는 프로세스
     * 기본적으로 유저가 수정은 불가능하다.
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
