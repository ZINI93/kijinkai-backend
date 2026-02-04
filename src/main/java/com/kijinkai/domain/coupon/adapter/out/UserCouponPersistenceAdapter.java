package com.kijinkai.domain.coupon.adapter.out;

import com.kijinkai.domain.coupon.adapter.out.entity.UserCouponJpaEntity;
import com.kijinkai.domain.coupon.adapter.out.mapper.UserCouponPersistenceMapper;
import com.kijinkai.domain.coupon.adapter.out.repository.usercoupon.UserCouponJpaEntityRepository;
import com.kijinkai.domain.coupon.application.port.out.UserCouponPersistencePort;
import com.kijinkai.domain.coupon.domain.modal.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class UserCouponPersistenceAdapter implements UserCouponPersistencePort {

    private final UserCouponJpaEntityRepository userCouponJpaEntityRepository;
    private final UserCouponPersistenceMapper userCouponPersistenceMapper;

    @Override
    public UserCoupon saveUserCoupon(UserCoupon userCoupon) {
        UserCouponJpaEntity userCouponJapEntity = userCouponPersistenceMapper.toUserCouponJapEntity(userCoupon);
        userCouponJapEntity = userCouponJpaEntityRepository.save(userCouponJapEntity);
        return userCouponPersistenceMapper.toUserCoupon(userCouponJapEntity);

    }

    @Override
    public Optional<UserCoupon> findByUserUuidAndUserCouponUuid(UUID userUuid, UUID userCouponUuid) {
        return userCouponJpaEntityRepository.findByUserUuidAndUserCouponUuid(userUuid, userCouponUuid)
                .map(userCouponPersistenceMapper::toUserCoupon);
    }

    @Override
    public Optional<UserCoupon> findByCouponUuid(UUID couponUuid) {
        return userCouponJpaEntityRepository.findByCouponUuid(couponUuid)
                .map(userCouponPersistenceMapper::toUserCoupon);

    }

    @Override
    public Optional<UserCoupon> findByUserCouponUuid(UUID userCouponUuid) {
        return userCouponJpaEntityRepository.findByUserCouponUuid(userCouponUuid)
                .map(userCouponPersistenceMapper::toUserCoupon);

    }

    @Override
    public Page<UserCoupon> findAllByUserUuid(UUID userUuid, Pageable pageable) {
        return userCouponJpaEntityRepository.findAllByUserUuid(userUuid,pageable)
                .map(userCouponPersistenceMapper::toUserCoupon);

    }

    @Override
    public void deleteUserCoupon(UserCoupon userCoupon) {
        UserCouponJpaEntity userCouponJapEntity = userCouponPersistenceMapper.toUserCouponJapEntity(userCoupon);
        userCouponJpaEntityRepository.delete(userCouponJapEntity);

    }

    @Override
    public Boolean existsByUserUuidAndCouponUuid(UUID userUuid, UUID couponUuid) {
        return userCouponJpaEntityRepository.existsByUserUuidAndCouponUuid(userUuid, couponUuid);
    }


}
