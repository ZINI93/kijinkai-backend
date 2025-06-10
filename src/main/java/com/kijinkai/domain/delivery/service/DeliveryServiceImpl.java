package com.kijinkai.domain.delivery.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.delivery.dto.DeliveryRequestDto;
import com.kijinkai.domain.delivery.dto.DeliveryResponseDto;
import com.kijinkai.domain.delivery.dto.DeliveryUpdateDto;
import com.kijinkai.domain.delivery.entity.Delivery;
import com.kijinkai.domain.delivery.exception.DeliveryNotFoundException;
import com.kijinkai.domain.delivery.factory.DeliveryFactory;
import com.kijinkai.domain.delivery.mapper.DeliveryMapper;
import com.kijinkai.domain.delivery.repository.DeliveryRepository;
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
    private final DeliveryFactory factory;
    private final DeliveryMapper mapper;

    @Override
    public DeliveryResponseDto createDeliveryWithValidate(String userUuid, DeliveryRequestDto requestDto) {

        log.info("Creating delivery for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Delivery delivery = factory.createDelivery(customer, requestDto);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        log.info("Created delivery for delivery uuid:{}", savedDelivery.getDeliveryUuid());
        return mapper.toResponse(savedDelivery);
    }

    private void updateDelivery(Delivery delivery, DeliveryUpdateDto updateDto){
        
        delivery.updateDelivery(
                updateDto.getReceiverName(),
                updateDto.getPostalCode(), 
                updateDto.getAddress1(),
                updateDto.getAddress2(),
                updateDto.getMemo()
        );
    }

    @Override
    public DeliveryResponseDto updateDeliveryWithValidate(String userUuid, String deliveryUuid, DeliveryUpdateDto updateDto) {

        log.info("Updating delivery for delivery uuid:{}", deliveryUuid);
        Delivery delivery = findDeliveryByUserUuidAndDeliveryUuid(userUuid, deliveryUuid);
        updateDelivery(delivery,updateDto);

        log.info("Updated delivery for delivery uuid:{}", delivery.getDeliveryUuid());
        return mapper.toResponse(delivery);
    }

    @Override
    public void deleteDelivery(String userUuid, String deliveryUuid) {
        log.info("Deleting delivery for delivery uuid:{}", deliveryUuid);
        Delivery delivery = findDeliveryByUserUuidAndDeliveryUuid(userUuid, deliveryUuid);;
        deliveryRepository.delete(delivery);
    }

    @Override
    public DeliveryResponseDto getDeliveryInfo(String userUuid, String deliveryUuid) {
        log.info("Searching delivery for delivery uuid:{}", userUuid);
        Delivery delivery = findDeliveryByUserUuidAndDeliveryUuid(userUuid, deliveryUuid);

        log.info("Searching delivery for delivery uuid:{}", delivery.getDeliveryUuid());
        return mapper.toResponse(delivery);
    }

    private Customer findCustomerByUserUuid(String userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException("userUuid: customer not found"));
    }

    private Delivery findDeliveryByUserUuidAndDeliveryUuid(String userUuid, String deliveryUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        return deliveryRepository.findByCustomerCustomerUuidAndDeliveryUuid(customer.getCustomerUuid(), deliveryUuid)
                .orElseThrow(() -> new DeliveryNotFoundException("CustomerUuidAndDeliveryUuid: delivery not found"));
    }
}
