package com.kijinkai.domain.coupon.adapter.out.repository.coupon;


import com.kijinkai.domain.coupon.adapter.out.entity.CouponJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponJpaEntityRepositoryCustom {

    Page<CouponJpaEntity> searchCoupons(CouponSearchCondition condition, Pageable pageable);
}
