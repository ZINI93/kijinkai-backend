package com.kijinkai.domain.delivery.application.dto;

import com.kijinkai.domain.delivery.domain.model.DeliveryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
@Builder
public class DeliveryRequestDto {

    @Schema(description = "주문상품코드", example = "OIR-....-....")
    private List<String> orderItemCodes;

    @Schema(description = "배송타입", example = "EMS")
    private DeliveryType deliveryType;

    @Schema(description = "추적번호", example = "1111-1111")
    private String trackingNumber;

    @Schema(description = "배송 요청사항", example = "피규어를 분해해서 공간을 활용해주세요.")
    private String deliveryRequest;
}
