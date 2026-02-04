package com.kijinkai.domain.coupon.adapter.out.mapper;


import com.kijinkai.domain.coupon.adapter.out.entity.CouponJpaEntity;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponPersistenceMapper {

    Coupon toCoupon(CouponJpaEntity couponJpaEntity);
    CouponJpaEntity toCouponJapEntity(Coupon coupon);


    List<Coupon> toCouponList(List<CouponJpaEntity> jpaEntities);
    List<CouponJpaEntity> toCouponJpaEntityList(List<Coupon> coupons);

}
