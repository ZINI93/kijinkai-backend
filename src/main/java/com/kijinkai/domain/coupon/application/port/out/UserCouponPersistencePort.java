package com.kijinkai.domain.coupon.application.port.out;

import com.kijinkai.domain.coupon.domain.modal.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserCouponPersistencePort {

    // 저장
    UserCoupon saveUserCoupon(UserCoupon userCoupon);


    // 조회
    Optional<UserCoupon> findByUserUuidAndUserCouponUuid(UUID userUuid, UUID userCouponUuid);
    Optional<UserCoupon> findByCouponUuid(UUID couponUuid);
    Optional<UserCoupon> findByUserCouponUuid(UUID couponUuid);
    Page<UserCoupon> findAllByUserUuid(UUID userUuid, Pageable pageable);


    // 삭제
    void deleteUserCoupon(UserCoupon userCoupon);


    // 검증
    Boolean existsByUserUuidAndCouponUuid(UUID userUuid, UUID couponUuid);
}
