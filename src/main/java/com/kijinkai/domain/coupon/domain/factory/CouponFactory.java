package com.kijinkai.domain.coupon.domain.factory;

import com.kijinkai.domain.coupon.application.dto.request.CouponCreateRequestDto;
import com.kijinkai.domain.coupon.domain.exception.CouponErrorCode;
import com.kijinkai.domain.coupon.domain.exception.CouponValidateException;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CouponFactory {

    public Coupon createCoupon(UUID userAdminUuid, CouponCreateRequestDto requestDto, String couponCode) {

        validateDiscountPolicy(requestDto);
        validateCreateInput(userAdminUuid, requestDto);

        return Coupon.builder()
                .couponUuid(UUID.randomUUID())
                .campaignUuid(requestDto.getCampaignUuid())
                .createdAdminUuid(userAdminUuid)
                .couponCode(couponCode)
                .couponName(requestDto.getCouponName())
                .couponIssuedType(requestDto.getCouponIssuedType())
                .discountType(requestDto.getDiscountType())
                .discountValue(requestDto.getDiscountValue())
                .totalQuantity(requestDto.getTotalQuantity())
                .issuedQuantity(0)
                .minOrderAmount(requestDto.getMinOrderAmount())
                .maxDiscountAmount(requestDto.getMaxDiscountAmount())
                .validFrom(requestDto.getValidFrom())
                .validUntil(requestDto.getValidUntil())
                .active(false)
                .build();
    }

    private void validateCreateInput(UUID userAdminUuid, CouponCreateRequestDto requestDto) {
        LocalDateTime now = LocalDateTime.now();

        if (userAdminUuid == null) {
            throw new UserNotFoundException("User uuid can't be null");
        }
        if (requestDto.getValidFrom().isAfter(requestDto.getValidUntil())) {
            throw new CouponValidateException(CouponErrorCode.INVALID_DATE_RANGE);
        }

        if (requestDto.getValidFrom().isAfter(now)) {
            throw new CouponValidateException(CouponErrorCode.START_DATE_BEFORE_NOW);
        }

        if (requestDto.getValidUntil().isBefore(now)) {
            throw new CouponValidateException(CouponErrorCode.END_DATE_BEFORE_NOW);
        }

    }

    private void validateDiscountPolicy(CouponCreateRequestDto requestDto) {
        DiscountType type = requestDto.getDiscountType();
        BigDecimal value = requestDto.getDiscountValue();


        if (type == DiscountType.PERCENT) {
            if (value.compareTo(BigDecimal.ZERO) <= 0 || value.compareTo(new BigDecimal("100")) > 0) {
                throw new CouponValidateException(CouponErrorCode.INVALID_DISCOUNT_RATE_RANGE);
            }

            if (requestDto.getMaxDiscountAmount() == null) {
                throw new CouponValidateException(CouponErrorCode.MAX_DISCOUNT_AMOUNT_REQUIRED);
            }
        }
        if (type == DiscountType.FIXED) {
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new CouponValidateException(CouponErrorCode.INVALID_DISCOUNT_AMOUNT);
            }
        }

    }

}
