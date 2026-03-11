package com.kijinkai.domain.shipment.service;

import com.kijinkai.domain.shipment.dto.shipment.request.ShipmentRequestDto;
import com.kijinkai.domain.shipment.dto.shipment.request.ShipmentTrackingRequestDto;
import com.kijinkai.domain.shipment.dto.shipment.request.ShipmentUpdateDto;
import com.kijinkai.domain.shipment.dto.shipment.response.ShipmentResponseDto;
import com.kijinkai.domain.shipment.dto.shipmentBoxItem.ShipmentBoxItemResponseDto;
import com.kijinkai.domain.shipment.entity.ShipmentEntity;
import com.kijinkai.domain.shipment.entity.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ShipmentService {
    // 생성
    List<UUID> createDeliveryBox(UUID userUuid, UUID deliveryUuid, ShipmentRequestDto requestDto);

    // 업데이트 및 배송 프로세스
    ShipmentResponseDto addTrackingNo(UUID userAdminUuid, UUID shipmentUuid, ShipmentTrackingRequestDto requestDto);
    void paidShipment(UUID deliveryUuid);
    ShipmentResponseDto updatePackedShipment(UUID userAdminUuid, UUID shipmentUuid, ShipmentUpdateDto updateDto);
    String delivered(UUID userUuid, String boxCode, ShipmentStatus status);
    void registerOrderPaymentToShipment(List<ShipmentEntity> shipmentEntities, UUID orderPaymentUuid);

    // 삭제 및 취소 (DeliveryService에서 위임받을 메서드)
    void cancelPacked(UUID userAdminUuid, UUID deliveryUuid);
    void deleteAllByDelivery(UUID deliveryUuid); // Delivery 취소 시 호출되는 내부 삭제 로직

    // 조회
    Page<ShipmentResponseDto> getShipmentsByStatus(UUID userUuid, ShipmentStatus shipmentStatus, Pageable pageable);
    List<ShipmentBoxItemResponseDto> getBoxItems(UUID userUuid, String boxCode);
    ShipmentResponseDto getPackagesByAdmin(UUID userAdminUuid, UUID deliveryUuid, Pageable pageable);
    int countShipmentByStatus(UUID userUuid, ShipmentStatus status);
}
