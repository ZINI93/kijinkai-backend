package com.kijinkai.domain.coupon.application.port.in.usercoupon;

import java.util.UUID;

public interface DeleteUserCouponUseCase {

    void deleteCoupon(UUID userUuid, UUID userCouponUuid);
}
