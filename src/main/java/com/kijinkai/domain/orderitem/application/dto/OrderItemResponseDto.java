package com.kijinkai.domain.orderitem.application.dto;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.InspectionStatus;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class OrderItemResponseDto {

    UUID orderItemUuid;
    UUID customerUuid;
    String orderCode;
    UUID orderUuid;
    String productLink;
    int quantity;
    String memo;
    BigDecimal priceOriginal;
    OrderItemStatus orderItemStatus;
    LocalDate createdAt;
    LocalDate updatedAt;
    String orderItemCode;
    BigDecimal depositBalance;
    InspectionStatus inspectionStatus;

    String rejectReason;

    List<UUID> orderItemUuids;


    String email;
    String phoneNumber;
    String name;


    @Schema(description = "수취인", example = "Park Jinhee")
    String recipientName;

    @Schema(description = "수취인 전화번호", example = "010-1111-1234")
    String recipientPhoneNumber;

    @Schema(description = "우편번호", example = "111-111")
    String zipcode;

    @Schema(description = "주소", example = "대구광역시 동구 신암로 ")
    String streetAddress;

    @Schema(description = "상세주소", example = "105-2길")
    String detailAddress;

    String pcc;
}
