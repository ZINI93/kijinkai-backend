package com.kijinkai.domain.delivery.application.dto;

import com.kijinkai.domain.delivery.adpater.out.persistence.entity.Carrier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class DeliveryRequestDto {

    @Schema(description = "배송회사", example = "JAPANPOST")
    private Carrier carrier;

    @Schema(description = "추적번호", example = "1111-1111")
    private String trackingNumber;

    @Schema(description = "배송 요청사항", example = "피규어를 분해해서 공간을 활용해주세요.")
    private String deliveryRequest;
}
