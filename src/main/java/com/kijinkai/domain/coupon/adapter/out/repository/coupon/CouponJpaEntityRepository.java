package com.kijinkai.domain.coupon.adapter.out.repository.coupon;

import com.kijinkai.domain.coupon.adapter.out.entity.CouponJpaEntity;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponJpaEntityRepository extends JpaRepository<CouponJpaEntity, Long>, CouponJpaEntityRepositoryCustom {


    //조회
    Optional<CouponJpaEntity> findByCouponCode(String couponCode);
    Optional<CouponJpaEntity> findByCouponUuid(UUID couponUuid);
    Optional<CouponJpaEntity> findByCampaignUuid(UUID campaignUuid);
    List<CouponJpaEntity> findAllByCouponUuidIn(List<UUID> couponUuids);


    // 검증.
    Boolean existsByCouponCode(String couponCode);
    Boolean existsByCampaignUuid(UUID campaignUuid);
}