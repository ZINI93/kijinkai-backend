package com.kijinkai.domain.coupon.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter // @Value 대신 @Getter + @NoArgsConstructor 조합을 더 선호하기도 함
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED) //
@Schema(description = "쿠폰 요청")
public class CouponIssuanceRequestDto {

    private String couponCode;
}
