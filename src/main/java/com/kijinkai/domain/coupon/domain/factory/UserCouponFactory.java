package com.kijinkai.domain.coupon.domain.factory;

import com.kijinkai.domain.coupon.domain.exception.CouponValidateException;
import com.kijinkai.domain.coupon.domain.modal.CouponIssuedType;
import com.kijinkai.domain.coupon.domain.modal.UserCoupon;
import com.kijinkai.domain.coupon.domain.modal.UserCouponStatus;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;


@Component
public class UserCouponFactory {


    public UserCoupon createUserCoupon(UUID userUuid, UUID couponUuid, LocalDateTime expiredAt, CouponIssuedType type) {

        validateCreateInput(userUuid, couponUuid, expiredAt);

        return UserCoupon.builder()
                .userCouponUuid(UUID.randomUUID())
                .userUuid(userUuid)
                .couponUuid(couponUuid)
                .issuedAt(LocalDateTime.now())
                .expiredAt(expiredAt)
                .userCouponStatus(UserCouponStatus.AVAILABLE)
                .couponIssuedType(type)
                .locked(false)
                .reusable(false)
                .build();

    }

    private void validateCreateInput(UUID userUuid, UUID couponUuid, LocalDateTime expiredAt ) {

        if (userUuid == null) {
            throw new CouponValidateException("발급 대상 유저 정보가 없습니다.");
        }
        if (couponUuid == null) {
            throw new CouponValidateException("발급할 쿠폰 정보가 없습니다.");
        }
        if (expiredAt == null) {
            throw new CouponValidateException("쿠폰의 만료일 정보가 없습니다.");
        }
    }



}
