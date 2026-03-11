package com.kijinkai.domain.delivery.application.mapper;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.application.dto.response.DeliveryCountResponseDto;
import com.kijinkai.domain.delivery.application.dto.response.DeliveryResponseDto;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {


    public DeliveryResponseDto toResponse(Delivery delivery) {
        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .customerUuid(delivery.getCustomerUuid())
                .recipientName(delivery.getRecipientName())
                .deliveryStatus(delivery.getDeliveryStatus())
                .recipientPhoneNumber(delivery.getRecipientPhoneNumber())
                .zipcode(delivery.getZipcode())
                .streetAddress(delivery.getStreetAddress())
                .detailAddress(delivery.getDetailAddress())
                .deliveryRequest(delivery.getDeliveryRequest())
                .build();
    }

    public DeliveryResponseDto toPaymentResponse(Delivery delivery){

        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .build();
    }

    public DeliveryResponseDto toCancelResponse(Delivery delivery){

        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .cancelReason(delivery.getCancelReason())
                .build();
    }

    public DeliveryResponseDto toDeliveryUuidResponse(Delivery delivery){

        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .build();
    }


    // 상품이랑. 스냅샷은 상세 조회
    public DeliveryResponseDto toAdminDeliveriesResponse(Delivery delivery, Customer customer) {
        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .customerUuid(delivery.getCustomerUuid())
                .name(customer.getLastName() + customer.getFirstName())
                .phoneNumber(customer.getPhoneNumber())
                .deliveryStatus(delivery.getDeliveryStatus())
                .deliveryType(delivery.getDeliveryType())
                .createdAt(delivery.getCreatedAt().toLocalDate())
                .updatedAt(delivery.getUpdatedAt().toLocalDate())
                .build();
    }



    public DeliveryResponseDto searchResponse(Delivery delivery) {
        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .customerUuid(delivery.getCustomerUuid())
                .recipientName(delivery.getRecipientName())
                .deliveryStatus(delivery.getDeliveryStatus())
                .recipientPhoneNumber(delivery.getRecipientPhoneNumber()).zipcode(delivery.getZipcode())
                .deliveryRequest(delivery.getDeliveryRequest())
                .deliveryType(delivery.getDeliveryType())
                .build();
    }

    public DeliveryCountResponseDto deliveryCount(int shippedCount, int deliveredCount){
        return DeliveryCountResponseDto.builder()
                .shippedCount(shippedCount)
                .deliveredCount(deliveredCount)
                .build();
    }


    public DeliveryResponseDto toRequestDeliveryOrderItemResponse(Delivery delivery, Customer customer, Page<OrderItemResponseDto> orderItems){


        return DeliveryResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .recipientName(delivery.getRecipientName())
                .recipientPhoneNumber(delivery.getRecipientPhoneNumber())
                .zipcode(delivery.getZipcode())
                .streetAddress(delivery.getStreetAddress())
                .detailAddress(delivery.getDetailAddress())
                .pcc(customer.getPcc())
                .requestOrderItems(orderItems)
                .build();
    }
}
