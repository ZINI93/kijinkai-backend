package com.kijinkai.domain.coupon.application.dto.request;

import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Schema(description = "쿠폰 업데이트 요청")
public class CouponUpdateRequestDto {

    private UUID campaignUuid;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private CouponIssuedType couponIssuedType;
    private Integer totalQuantity;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
}
