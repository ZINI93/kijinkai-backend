package com.kijinkai.domain.coupon.application.mapper;

import com.kijinkai.domain.coupon.application.dto.response.UserCouponResponseDto;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import com.kijinkai.domain.coupon.domain.modal.UserCoupon;
import org.springframework.stereotype.Component;

@Component
public class UserCouponMapper {


    public UserCouponResponseDto toResponse(UserCoupon userCoupon, Coupon coupon){

        return UserCouponResponseDto.builder()
                .userCouponUuid(userCoupon.getUserCouponUuid())
                .couponCode(coupon.getCouponCode())
                .couponName(coupon.getCouponName())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDisCountAmount(coupon.getMaxDiscountAmount())
                .validFrom(coupon.getValidFrom())
                .validUntil(coupon.getValidUntil())
                .userCouponStatus(userCoupon.getUserCouponStatus())
                .issuedAt(userCoupon.getIssuedAt())
                .usedAt(userCoupon.getUsedAt())
                .build();

    }
}
