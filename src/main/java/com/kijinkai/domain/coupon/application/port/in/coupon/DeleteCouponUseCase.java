package com.kijinkai.domain.coupon.application.port.in.coupon;

import java.util.UUID;

public interface DeleteCouponUseCase {

    void deleteCoupon(UUID userAdminUuid, UUID couponUuid);
}
