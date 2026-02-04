package com.kijinkai.domain.coupon.application.dto.response;

import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "쿠폰 응답")
public class CouponResponseDto {

    private UUID couponUuid;
    private UUID campaignUuid;
    private String couponName;
    private String couponCode;
    private DiscountType discountType;
    private CouponIssuedType couponIssuedType;
    private BigDecimal discountValue;
    private Integer totalQuantity;
    private Integer issuedQuantity;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;




}
