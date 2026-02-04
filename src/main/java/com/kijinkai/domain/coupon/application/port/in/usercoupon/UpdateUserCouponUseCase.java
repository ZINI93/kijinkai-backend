package com.kijinkai.domain.coupon.application.port.in.usercoupon;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateUserCouponUseCase {

    void useCoupon(UUID userUuid, UUID userCouponUuid, BigDecimal discountAmount);
}
