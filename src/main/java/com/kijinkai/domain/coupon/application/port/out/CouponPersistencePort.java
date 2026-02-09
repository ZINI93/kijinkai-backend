package com.kijinkai.domain.coupon.application.port.out;

import com.kijinkai.domain.coupon.adapter.out.repository.coupon.CouponSearchCondition;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponPersistencePort {

    // 저장
    Coupon saveCoupon(Coupon coupon);


    //조회

    Optional<Coupon> findByCouponUuid(UUID couponUuid);
    Optional<Coupon> findByCouponCode(String couponCode);
    Optional<Coupon> findByCampaignUuid(UUID campaignUuid);
    Page<Coupon> searchCoupon(CouponSearchCondition condition, Pageable pageable);
    List<Coupon> findAllByCouponUuids(List<UUID> couponUuids);


    //삭제
    void deleteCoupon(Coupon coupon);


    // 검증
    Boolean existsByCouponCode(String couponCode);
    Boolean existsByCampaignUuid(UUID campaignUuid);
}
