package com.kijinkai.domain.shipment.mapper;


import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import com.kijinkai.domain.shipment.dto.shipment.response.ShipmentBoxResponseDto;
import com.kijinkai.domain.shipment.dto.shipmentBoxItem.ShipmentBoxItemResponseDto;
import com.kijinkai.domain.shipment.dto.shipment.response.ShipmentResponseDto;
import com.kijinkai.domain.shipment.entity.ShipmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShipmentMapper {


    public ShipmentResponseDto toShipmentResponseDto(Delivery delivery, Customer customer, Page<ShipmentBoxResponseDto> shipmentPages){

        return ShipmentResponseDto.builder()
                .deliveryUuid(delivery.getDeliveryUuid())
                .recipientName(delivery.getRecipientName())
                .recipientPhoneNumber(delivery.getRecipientPhoneNumber())
                .deliveryType(delivery.getDeliveryType())
                .zipcode(delivery.getZipcode())
                .streetAddress(delivery.getStreetAddress())
                .detailAddress(delivery.getDetailAddress())
                .pcc(customer.getPcc() != null ? customer.getPcc() : "미등록")
                .shipmentPages(shipmentPages)
                .createdAt(delivery.getCreatedAt().toLocalDate())
                .updatedAt(delivery.getUpdatedAt().toLocalDate())
                .build();
    }


    public ShipmentBoxResponseDto toShipmentListResponseDto(ShipmentEntity shipment, List<ShipmentBoxItemResponseDto> boxItemResponseDtos){

        return ShipmentBoxResponseDto.builder()
                .shipmentUuid(shipment.getShipmentUuid())
                .boxCode(shipment.getBoxCode())
                .trackingNo(shipment.getTrackingNo() != null ? shipment.getTrackingNo() : "미입력")
                .weight(shipment.getTotalWeight())
                .shipmentFee(shipment.getShippingFee())
                .boxItems(boxItemResponseDtos)
                .build();
    }

    public ShipmentResponseDto toShipmentUuidResponseDto(ShipmentEntity shipment){

        return ShipmentResponseDto.builder()
                .shipmentUuid(shipment.getShipmentUuid())
                .build();
    }
}
