package com.kijinkai.domain.coupon.application.port.in.usercoupon;

import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.UserCoupon;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateUserCouponUseCase {


    UserCoupon createUserCoupon(UUID userUuid, UUID couponUuid, LocalDateTime expired, CouponIssuedType type);
}
