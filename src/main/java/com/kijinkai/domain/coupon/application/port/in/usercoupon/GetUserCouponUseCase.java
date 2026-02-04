package com.kijinkai.domain.coupon.application.port.in.usercoupon;

import com.kijinkai.domain.coupon.application.dto.response.UserCouponResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface GetUserCouponUseCase {

    UserCouponResponseDto getMyCouponInfo(UUID userUuid,UUID userCouponUuid);

    Page<UserCouponResponseDto> getMyCoupons(UUID userUuid, Pageable pageable);

    BigDecimal discountValue(UUID userUuid, UUID userCouponUuid, BigDecimal orderAmount);
}
