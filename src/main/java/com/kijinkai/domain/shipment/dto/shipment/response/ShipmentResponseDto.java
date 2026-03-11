package com.kijinkai.domain.shipment.dto.shipment.response;

import com.kijinkai.domain.delivery.domain.model.DeliveryType;
import com.kijinkai.domain.orderitem.application.dto.OrderItemResponseDto;
import com.kijinkai.domain.shipment.dto.shipmentBoxItem.ShipmentBoxItemResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ShipmentResponseDto {

    private UUID deliveryUuid;
    String boxCodes;
    Double weight;
    BigDecimal shipmentFee;
    String trackingNo;
    List<ShipmentBoxItemResponseDto> shipmentBoxItems;
    UUID shipmentUuid;
    DeliveryType deliveryType;

    private Page<ShipmentBoxResponseDto> shipmentPages;
    private List<ShipmentBoxItemResponseDto> shipmentBoxItemlist;




    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String name;
    private String phoneNumber;
    private Page<OrderItemResponseDto> requestOrderItems;
    private String pcc;


    @Schema(description = "수취인", example = "Park Jinhee")
    private String recipientName;

    @Schema(description = "수취인 전화번호", example = "010-1111-1234")
    private String recipientPhoneNumber;

    @Schema(description = "우편번호", example = "111-111")
    private String zipcode;

    @Schema(description = "주소", example = "대구광역시 동구 신암로 ")
    private String streetAddress;

    @Schema(description = "상세주소", example = "105-2길")
    private String detailAddress;
}
