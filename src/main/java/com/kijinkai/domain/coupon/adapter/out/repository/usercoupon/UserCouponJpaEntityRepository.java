package com.kijinkai.domain.coupon.adapter.out.repository.usercoupon;

import com.kijinkai.domain.coupon.adapter.out.entity.UserCouponJpaEntity;
import com.kijinkai.domain.coupon.domain.modal.UserCoupon;
import io.jsonwebtoken.security.Jwks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserCouponJpaEntityRepository extends JpaRepository<UserCouponJpaEntity, Long> {

    Optional<UserCouponJpaEntity> findByUserUuidAndUserCouponUuid(UUID userUuid, UUID userCouponUuid);
    Optional<UserCouponJpaEntity> findByCouponUuid(UUID couponUuid);
    Optional<UserCouponJpaEntity> findByUserCouponUuid(UUID couponUuid);
    Page<UserCouponJpaEntity> findAllByUserUuid(UUID userUuid, Pageable pageable);

    Boolean existsByUserUuidAndCouponUuid(UUID userUuid, UUID couponUuid);
}