package com.kijinkai.domain.coupon.application.dto.request;


import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter // @Value 대신 @Getter + @NoArgsConstructor 조합을 더 선호하기도 함
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED) //
@Schema(description = "쿠폰 요청")
public class CouponCreateRequestDto {

    private UUID campaignUuid;
    private String couponName;
    private DiscountType discountType;
    private CouponIssuedType couponIssuedType;
    private BigDecimal discountValue;
    private Integer totalQuantity;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

}
