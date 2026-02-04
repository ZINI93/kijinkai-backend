package com.kijinkai.domain.coupon.application.mapper;

import com.kijinkai.domain.coupon.application.dto.response.CouponResponseDto;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public CouponResponseDto toCreateResponse(Coupon coupon) {

        return CouponResponseDto.builder()
                .couponCode(coupon.getCouponCode())
                .discountType(coupon.getDiscountType())
                .couponName(coupon.getCouponName())
                .couponIssuedType(coupon.getCouponIssuedType())
                .discountValue(coupon.getDiscountValue())
                .totalQuantity(coupon.getTotalQuantity())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .validFrom(coupon.getValidFrom())
                .validUntil(coupon.getValidUntil())
                .build();
    }


    public CouponResponseDto toResponse(Coupon coupon) {

        return CouponResponseDto.builder()
                .couponUuid(coupon.getCouponUuid())
                .couponCode(coupon.getCouponCode())
                .discountType(coupon.getDiscountType())
                .couponName(coupon.getCouponName())
                .couponIssuedType(coupon.getCouponIssuedType())
                .discountValue(coupon.getDiscountValue())
                .totalQuantity(coupon.getTotalQuantity())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .validFrom(coupon.getValidFrom())
                .validUntil(coupon.getValidUntil())
                .active(coupon.isActive())
                .build();
    }
}

