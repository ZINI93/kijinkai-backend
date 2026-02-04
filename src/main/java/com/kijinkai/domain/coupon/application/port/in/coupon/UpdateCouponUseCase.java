package com.kijinkai.domain.coupon.application.port.in.coupon;

import com.kijinkai.domain.coupon.application.dto.request.CouponIssuanceRequestDto;
import com.kijinkai.domain.coupon.application.dto.request.CouponUpdateRequestDto;
import com.kijinkai.domain.coupon.application.dto.response.CouponResponseDto;

import java.util.UUID;

public interface UpdateCouponUseCase {

    String activeCoupon(UUID userAdminUuid, UUID couponUuid);

    CouponResponseDto updateCoupon(UUID userAdminUuid, UUID couponUuid, CouponUpdateRequestDto requestDto);
    void issueCoupon(UUID userUuid, UUID couponUuid);
    void issueByCouponCode(UUID userUuid, CouponIssuanceRequestDto requestDto);


}
