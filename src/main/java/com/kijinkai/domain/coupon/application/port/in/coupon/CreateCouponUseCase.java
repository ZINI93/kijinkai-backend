package com.kijinkai.domain.coupon.application.port.in.coupon;

import com.kijinkai.domain.coupon.application.dto.request.CouponCreateRequestDto;
import com.kijinkai.domain.coupon.application.dto.response.CouponResponseDto;

import java.util.UUID;

public interface CreateCouponUseCase {

    CouponResponseDto createCoupon(UUID userAdminUuid, CouponCreateRequestDto requestDto);

}
