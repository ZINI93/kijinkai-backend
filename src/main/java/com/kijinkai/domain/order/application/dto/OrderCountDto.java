package com.kijinkai.domain.order.application.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "주문 카운드 응답")
public class OrderCountDto {

    UUID customerUuid;
    Long orderCount;
}
