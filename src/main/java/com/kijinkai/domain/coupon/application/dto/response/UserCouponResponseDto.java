package com.kijinkai.domain.coupon.application.dto.response;

import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import com.kijinkai.domain.coupon.domain.modal.UserCouponStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "유저 쿠폰 응답")
public class UserCouponResponseDto {

    private UUID userCouponUuid;
    private String couponCode;
    private String couponName;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDisCountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    private UserCouponStatus userCouponStatus;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;
}
