package com.kijinkai.domain.coupon.application.port.in.coupon;

import com.kijinkai.domain.coupon.application.dto.response.CouponResponseDto;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface GetCouponUseCase {

    CouponResponseDto getCouponInfo(String couponCode);

    Page<CouponResponseDto> getCoupons(UUID campaignUuid, String couponCode, DiscountType type, Integer minTotalQuantity, Integer maxTotalQuantity, LocalDate validFrom, LocalDate validUntil, Boolean isActive, Pageable pageable);
}
