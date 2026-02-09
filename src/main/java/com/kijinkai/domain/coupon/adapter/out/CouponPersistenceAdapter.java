package com.kijinkai.domain.coupon.adapter.out;

import com.kijinkai.domain.coupon.adapter.out.entity.CouponJpaEntity;
import com.kijinkai.domain.coupon.adapter.out.mapper.CouponPersistenceMapper;
import com.kijinkai.domain.coupon.adapter.out.repository.coupon.CouponJpaEntityRepository;
import com.kijinkai.domain.coupon.adapter.out.repository.coupon.CouponSearchCondition;
import com.kijinkai.domain.coupon.application.port.out.CouponPersistencePort;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CouponPersistenceAdapter implements CouponPersistencePort {

    private final CouponJpaEntityRepository couponJpaEntityRepository;
    private final CouponPersistenceMapper couponPersistenceMapper;


    @Override
    public Coupon saveCoupon(Coupon coupon) {
        CouponJpaEntity couponJapEntity = couponPersistenceMapper.toCouponJapEntity(coupon);
        couponJapEntity = couponJpaEntityRepository.save(couponJapEntity);
        return couponPersistenceMapper.toCoupon(couponJapEntity);
    }

    @Override
    public Optional<Coupon> findByCouponUuid(UUID couponUuid) {
        return couponJpaEntityRepository.findByCouponUuid(couponUuid)
                .map(couponPersistenceMapper::toCoupon);
    }

    @Override
    public Optional<Coupon> findByCouponCode(String couponCode) {
        return couponJpaEntityRepository.findByCouponCode(couponCode)
                .map(couponPersistenceMapper::toCoupon);

        }

    @Override
    public Optional<Coupon> findByCampaignUuid(UUID campaignUuid) {
        return couponJpaEntityRepository.findByCampaignUuid(campaignUuid)
                .map(couponPersistenceMapper::toCoupon);
    }

    @Override
    public Page<Coupon> searchCoupon(CouponSearchCondition condition, Pageable pageable) {
        return couponJpaEntityRepository.searchCoupons(condition, pageable)
                .map(couponPersistenceMapper::toCoupon);
    }

    @Override
    public List<Coupon> findAllByCouponUuids(List<UUID> couponUuids) {
        return couponJpaEntityRepository.findAllByCouponUuidIn(couponUuids)
                .stream().map(couponPersistenceMapper::toCoupon).toList();
    }

    @Override
    public void deleteCoupon(Coupon coupon) {
        CouponJpaEntity couponJapEntity = couponPersistenceMapper.toCouponJapEntity(coupon);
        couponJpaEntityRepository.delete(couponJapEntity);
    }

    @Override
    public Boolean existsByCouponCode(String couponCode) {
        return couponJpaEntityRepository.existsByCouponCode(couponCode);
    }

    @Override
    public Boolean existsByCampaignUuid(UUID campaignUuid) {
        return couponJpaEntityRepository.existsByCampaignUuid(campaignUuid);
    }
}
